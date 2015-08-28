package handlers;

import headers.AllowedMethods;
import headers.MimeTypes;
import headers.ResponseCode;
import headers.ResponseCodes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import request.HttpRequest;
import response.HttpResponse;
import server.Settings;
import templates.TemplateGenerator;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * ServerHandler на основе HttpRequest формирует HttpResponse
 *
 * Created by vadim on 23.02.15.
 */

public class ServerHandler extends SimpleChannelInboundHandler<HttpRequest> {

    /*
    В Netty 5.0 метод переименован в messageReceived(...).
    Вызывается для каждого объекта типа HttpRequest из pipeline
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpRequest httpRequest) throws IOException {
        HttpResponse response;
        if (!httpRequest.isValid()) {
            sendError(ctx, ResponseCodes.BAD_REQUEST);
            return;
        }

        String method = httpRequest.getMethod();
        if (!AllowedMethods.contains(method)) {
            sendError(ctx, ResponseCodes.METHOD_NOT_ALLOWED);
            return;
        }

        String uri = correctURI(httpRequest.getUri());
        String documentRoot = Settings.getDocumentRoot();

        Path pathToFile = Paths.get(documentRoot, uri);
        if (Files.isDirectory(pathToFile)) {
            pathToFile = Paths.get(documentRoot, uri, "/index.html");
            if (!Files.exists(pathToFile)) {
                sendError(ctx, ResponseCodes.FORBIDDEN);
                return;
            }
        }

        FileRegion region;
        if (Files.exists(pathToFile) && !Files.isHidden(pathToFile)) {
            FileInputStream in = new FileInputStream(pathToFile.toString());
            region = new DefaultFileRegion(
                    in.getChannel(), 0, pathToFile.toFile().length()
            );
        }
        else {
            sendError(ctx, ResponseCodes.NOT_FOUND);
            return;
        }

        response = new HttpResponse(ResponseCodes.OK);
        response.getHeader().setDefaultHeaders();
        response.getHeader().setHeader("Content-Type", getContentType(uri));
        response.getHeader().setHeader("Content-Length", String.valueOf(region.count()));

        if (method.equals("HEAD")) {
            ByteBuf byteBuf = response.toByteBuf();
            ctx.writeAndFlush(byteBuf).addListener(ChannelFutureListener.CLOSE);
            return;
        }

        Channel channel = ctx.channel();
        channel.eventLoop().execute(() -> {
            channel.write(response.toByteBuf());
            channel.writeAndFlush(region).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    future.addListener(ChannelFutureListener.CLOSE);
                }
            });
        });
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

    private void sendError(ChannelHandlerContext ctx, ResponseCode responseCode) {
        HttpResponse responseError = new HttpResponse(responseCode);
        responseError.setContext(TemplateGenerator.generate(responseCode));
        responseError.getHeader().setDefaultHeaders();
        responseError.getHeader().setHeader("Content-Type", MimeTypes.getDefaultMimeType());
        responseError.getHeader().setHeader("Content-Length", String.valueOf(responseError.getContext().length));
        ByteBuf byteBuf = responseError.toByteBuf();
        ctx.writeAndFlush(byteBuf).addListener(ChannelFutureListener.CLOSE);
    }

    private String correctURI(String uri) {
        return uri.replace("/..", "");
    }
}