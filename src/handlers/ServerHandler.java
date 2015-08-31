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
import java.io.FileNotFoundException;
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
    public void channelRead0(ChannelHandlerContext ctx, HttpRequest httpRequest) throws FileNotFoundException {
        ResponseCode responseCode = getResponseCode(httpRequest);

        if (responseCode == ResponseCodes.OK) {
            String uri = httpRequest.getUri();
            String documentRoot = Settings.getDocumentRoot();
            Path pathToFile = Paths.get(documentRoot, uri);

            FileInputStream in = new FileInputStream(pathToFile.toString());
            FileRegion region = new DefaultFileRegion(
                    in.getChannel(), 0, pathToFile.toFile().length()
            );

            HttpResponse response = new HttpResponse(responseCode);
            response.getHeader().setDefaultHeaders()
                    .setHeader("Content-Type", getContentType(uri))
                    .setHeader("Content-Length", String.valueOf(region.count()));

            if (httpRequest.getMethod().equals("HEAD")) {
                sendResponse(ctx, response);
                return;
            }

            sendResponse(ctx, response, region);
        }

        else {
            HttpResponse responseError = new HttpResponse(responseCode);
            byte[] context = TemplateGenerator.generate(responseCode);
            responseError.setContext(context);
            responseError.getHeader().setDefaultHeaders()
                    .setHeader("Content-Type", MimeTypes.getDefaultMimeType())
                    .setHeader("Content-Length", String.valueOf(context.length));
            sendResponse(ctx, responseError);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

    private ResponseCode getResponseCode(HttpRequest request) {
        if (!request.isValid())
            return ResponseCodes.BAD_REQUEST;

        String method = request.getMethod();
        if (!AllowedMethods.contains(method))
            return ResponseCodes.METHOD_NOT_ALLOWED;

        String uri = request.getUri();
        String documentRoot = Settings.getDocumentRoot();

        Path pathToFile = Paths.get(documentRoot, uri);
        if (Files.isDirectory(pathToFile)) {
            pathToFile = Paths.get(documentRoot, uri, Settings.INDEX);
            if (!Files.exists(pathToFile))
                return ResponseCodes.FORBIDDEN;
        }

        try {
            if (Files.exists(pathToFile) && !Files.isHidden(pathToFile))
                return ResponseCodes.OK;
            else
                return ResponseCodes.NOT_FOUND;
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseCodes.NOT_FOUND;
        }
    }

    private String getContentType(String uri) {
        return MimeTypes.getMimeType(uri.substring(uri.lastIndexOf(".") + 1).toLowerCase());
    }

    private void sendResponse(ChannelHandlerContext ctx, HttpResponse response) {
        ByteBuf byteBuf = response.toByteBuf();
        ctx.writeAndFlush(byteBuf).addListener(ChannelFutureListener.CLOSE);
    }

    private void sendResponse(ChannelHandlerContext ctx, HttpResponse response, FileRegion region) {
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
}