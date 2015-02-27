import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import java.nio.file.*;

/**
 * Created by vadim on 23.02.15.
 */
public class HttpRequestDecoder extends ChannelDuplexHandler {
    private static final String LOG_TAG = HttpRequestDecoder.class.getName();
    private static final String DOCUMENT_ROOT = "/home/vadim/http-test-suite";

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

        System.err.println(request);
        String requestMethod = getRequestMethod(request.toString());
        String requestPath = getRequestPath(request.toString());
        if (requestPath.endsWith("/"))
            requestPath += "index.html";

        Path pathToFile = Paths.get(DOCUMENT_ROOT, requestPath);
        System.err.println("PATH: " + pathToFile.toString());

        HttpResponseObject httpResponseObject = new HttpResponseObject(requestPath, requestMethod);
        ctx.fireChannelRead(httpResponseObject.getResponse());
        //super.channelRead(ctx, byteBuf);
    }


    private String getRequestPath(String request) {
        int beginPath = request.indexOf(" ") + 1;
        int endPath = request.indexOf(" ", beginPath);

        // М.б. есть что-то лучше?
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

    private String getRequestMethod(String request) {
        return request.substring(0, request.indexOf(" "));
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println(LOG_TAG + "Write!");

        //ctx.write(message, promise);
        super.write(ctx, msg, promise);
    }
}
