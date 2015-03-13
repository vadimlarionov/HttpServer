package handlers;

import headers.MimeTypes;
import headers.ResponseCodes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import request.HttpRequest;
import response.HttpResponse;
import server.Settings;

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

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpRequest httpRequest) throws IOException {
        if (!httpRequest.isValid()) {
            sendError(ctx, ResponseCodes.BAD_REQUEST);
            return;
        }

        String method = httpRequest.getMethod();
        if (!method.equals("GET") && !method.equals("HEAD")) {
            sendError(ctx, ResponseCodes.METHOD_NOT_ALLOWED);
            return;
        }

        String uri = correctURI(httpRequest.getUri());
        String documentRoot = Settings.getDocumentRoot();
        byte[] context;

        Path pathToFile = Paths.get(documentRoot, uri);
        if (Files.isDirectory(pathToFile)) {
            pathToFile = Paths.get(documentRoot, uri, "/index.html");
            if (!Files.exists(pathToFile)) {
                sendError(ctx, ResponseCodes.FORBIDDEN);
                return;
            }
        }

        if (Files.exists(pathToFile) && !Files.isHidden(pathToFile)) {
            context = Files.readAllBytes(pathToFile);
        }
        else {
            sendError(ctx, ResponseCodes.NOT_FOUND);
            return;
        }

        int contextLength = context != null ? context.length : 0;

        HttpResponse response = new HttpResponse(200);
        if (method.equals("GET"))
            response.setContext(context);
        response.setHeader("Server", "LarionovServer");
        response.setHeader("Content-Type", getContentType(uri));
        response.setHeader("Content-Length", String.valueOf(contextLength));
        response.setHeader("Connection", "close");
        response.setHeader("Date", (new Date()).toString());

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

    private void sendError(ChannelHandlerContext ctx, int responseCode) {
        HttpResponse responseError = new HttpResponse(responseCode);
        responseError.createErrorResponse();
        ByteBuf byteBuf = responseError.getResponse();
        ctx.writeAndFlush(byteBuf).addListener(ChannelFutureListener.CLOSE);
    }

    private String correctURI(String uri) {
        return uri.replace("/..", "");
    }
}