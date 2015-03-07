import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by vadim on 23.02.15.
 */
public class HttpRequestDecoder extends ChannelInboundHandlerAdapter {
    private static final String LOG_TAG = HttpRequestDecoder.class.getName();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(LOG_TAG + " channelRead()");

        StringBuilder request = new StringBuilder();
        ByteBuf in = (ByteBuf) msg;
        char ch = 1;
        while (in.isReadable() && ch != '\n') {
            ch = (char) in.readByte();
            request.append(ch);
        }

        String requestMethod = getRequestMethod(request.toString());
        String requestPath = getRequestPath(request.toString());
        if (requestPath.endsWith("/"))
            requestPath += "index.html";

        HttpRequest httpRequest = new HttpRequest(requestMethod, requestPath);
        ctx.fireChannelRead(httpRequest);
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

}
