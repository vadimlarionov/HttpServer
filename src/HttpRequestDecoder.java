import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import java.nio.file.*;

/**
 * Created by vadim on 23.02.15.
 */
public class HttpRequestDecoder extends ChannelInboundHandlerAdapter {
    private static final String LOG_TAG = HttpRequestDecoder.class.getName();
    private static final String DOCUMENT_ROOT = "/home/vadim";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //super.channelRead(ctx, msg);
        System.out.println(LOG_TAG + " channelRead()");

        // Не самый лучший вариант
        // Class StringEncoder http://netty.io/4.0/api/io/netty/handler/codec/string/StringEncoder.html
        StringBuilder request = new StringBuilder();
        ByteBuf in = (ByteBuf) msg;
        while (in.isReadable()) {
            request.append((char) in.readByte());
        }

        String requestPath = getRequestPath(request.toString());
        if (requestPath.equals("/"))
            requestPath = "/index.html";

        Path path = Paths.get(DOCUMENT_ROOT, requestPath);

        byte[] bytes = null;
        if (Files.exists(path)) {
            System.out.println("File exists");
            if (!Files.isDirectory(path)) {
                bytes = Files.readAllBytes(path);
            }
            else {
                System.out.println("File is directory");
            }
        }
        else {
            System.out.println("File not exist");
        }

        //HttpResponse response = new HttpResponse();
        //String context = "<html><body>Hello</body></html>";
        //String ret = response.getResponseHeader(200, bytes.length);
        //ret += context;

        HttpResponse response = new HttpResponse();
        assert bytes != null;
        String headers = response.getResponseHeader(200, bytes.length);

        ByteBuf byteBuf = Unpooled.copiedBuffer(headers.getBytes(), bytes);
        super.channelRead(ctx, byteBuf);
    }


    private String getRequestPath(String request) {
        int beginPath = request.indexOf(" ") + 1;
        int endPath = request.indexOf(" ", beginPath);

        // М.б. есть что-то асинхронное?
        String path = request.substring(beginPath, endPath);
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        int queryIndex = path.indexOf("?");
        if (queryIndex != -1)
            path = path.substring(0, queryIndex);

        return path;
    }
}
