import io.netty.buffer.ByteBuf;
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
        StringBuilder request = new StringBuilder();
        ByteBuf in = (ByteBuf) msg;
        while (in.isReadable()) {
            request.append((char) in.readByte());
        }

        String requestPath = getRequestPath(request.toString());
        if (requestPath.equals("/"))
            requestPath = "/index.html";

        Path pathToFile = Paths.get(DOCUMENT_ROOT, requestPath);
        System.err.println("PATH: " + pathToFile.toString());

        HttpResponseObject httpResponseObject = new HttpResponseObject(requestPath);
        ctx.fireChannelRead(httpResponseObject.getResponse());
        //super.channelRead(ctx, byteBuf);
    }


    private String getRequestPath(String request) {
        int beginPath = request.indexOf(" ") + 1;
        int endPath = request.indexOf(" ", beginPath);

        // М.б. есть что-то асинхронное?
        // Class StringEncoder http://netty.io/4.0/api/io/netty/handler/codec/string/StringEncoder.html
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
