import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

/**
 * ServerHandler на основе HttpRequest формирует HttpResponse
 *
 * Created by vadim on 23.02.15.
 */

public class ServerHandler extends SimpleChannelInboundHandler<HttpRequest> {
    private static final String LOG_TAG = ServerHandler.class.getName();

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpRequest httpRequest) throws IOException {
        System.out.println(LOG_TAG + " channelRead0() ");

        // Предполагается, что URI корректный
        String uri = httpRequest.getUri();

        Path pathToFile = Paths.get(Settings.getDocumentRoot(), uri);
        // Здесь должна быть проверка файла

        System.err.println(pathToFile.toString());
        byte[] context = Files.readAllBytes(pathToFile);

        HttpResponse response = new HttpResponse(200);
        response.setContext(context);
        response.setHeader("Server", "LarionovServer");
        response.setHeader("Content-Type", getContentType(uri));
        response.setHeader("Content-Length", String.valueOf(context.length));
        response.setHeader("Connection", "close");
        response.setHeader("Date: ", (new Date()).toString());

        ByteBuf buf = response.getResponse();
        ctx.writeAndFlush(buf).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

    private String getContentType(String uri) {
        return MimeTypes.getMimeType(uri.substring(uri.lastIndexOf(".") + 1).toLowerCase());
    }

}