import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

/**
 * Created by vadim on 23.02.15.
 */
public class HttpResponseObject {

    private Path pathToFile = null;
    private byte[] context = null;
    private int statusCode = -1;
    private String requestMethod;

    private static String DOCUMENT_ROOT = Settings.getDocumentRoot();


    public HttpResponseObject(String requestPath, String requestMethod) throws IOException {

        if (requestPath.equals("/"))
            requestPath = "/index.html";

        // Вынести в отдельный метод
        Path pathToFile = Paths.get(DOCUMENT_ROOT, requestPath);
        this.pathToFile = pathToFile; // ! ! !

        if (!pathToFile.getParent().toString().contains(DOCUMENT_ROOT)) {
            System.err.println(pathToFile.getParent().toString());
            context = null;
            statusCode = 403;
        }
        else if (Files.exists(pathToFile)) {
            if (!Files.isDirectory(pathToFile)) {
                context = Files.readAllBytes(pathToFile);
                statusCode = 200;
            } else {
                // Это директория. 403 or 404?
                context = null;
                statusCode = 403;
            }
        } else {
            if (pathToFile.toString().endsWith("index.html"))
                statusCode = 403;
            else
                statusCode = 404;

            System.err.print(pathToFile);
            context = null;
        }

        // Отдельно должно быть
        this.requestMethod = requestMethod.toUpperCase();
        if (!this.requestMethod.equals("GET") && !this.requestMethod.equals("HEAD")) {
            statusCode = 405;
            context = null;
        }
    }

    private String getResponseHeader() {
        StringBuilder headers = new StringBuilder();

        headers.append("HTTP/1.1 ").append(getResponseCode()).append("\r\n");
        headers.append("Server: LarionovServer\r\n");
        headers.append("Content-Type: ").append(getMimeType()).append("\r\n");
        headers.append("Content-Length: ").append(getContentLength()).append("\r\n");
        headers.append("Connection: close\r\n");
        headers.append("Date: ").append(new Date()).append("\r\n");

        headers.append("\r\n");
        return headers.toString();
    }

    private String getResponseCode() {
        return statusCode + " " + ResponseCodes.getResponseCodeValue(statusCode);
    }
    private String getMimeType() {
        if (statusCode >= 400) {
            context = (getResponseCode() + "\r\n") .getBytes();
            return "text/html";
        }

        String path = pathToFile.toString();

        String fileType = path.substring(path.lastIndexOf(".") + 1).toLowerCase();
        System.out.println("fileType: " + fileType);
        String mimeType = MimeTypes.getMimeType(fileType);
        return mimeType;
    }

    private int getContentLength() {
//        if (requestMethod.equals("HEAD"))
//            return 0;
        return context != null ? context.length : 0;
    }

    public ByteBuf getResponse() {
        String headers = getResponseHeader();

        // Формируем response и возвращаем
        ByteBuf response;
        System.err.println("Headers");
        System.out.println(headers);

        if (context != null) {
            if (requestMethod.equals("HEAD"))
                response = Unpooled.copiedBuffer(headers.getBytes());
            else
                response = Unpooled.copiedBuffer(headers.getBytes(), context);
        }
        else
            response = Unpooled.copiedBuffer(headers.getBytes());

        response.retain();

        return response;
    }


    private void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod.toUpperCase();

    }
}
