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

    private static final String DOCUMENT_ROOT = "/home/vadim";

    public HttpResponseObject() {}
    public HttpResponseObject(String requestPath) throws IOException {

        if (requestPath.equals("/"))
            requestPath = "/index.html";

        // Вынести в отдельный метод
        Path pathToFile = Paths.get(DOCUMENT_ROOT, requestPath);
        System.out.println("LALALA: " + pathToFile.toString());
        this.pathToFile = pathToFile; // ! ! !
        if (!pathToFile.getParent().toString().equals(DOCUMENT_ROOT)) {
            context = null;
            statusCode = 403;
        }
        else if (Files.exists(pathToFile)) {
            if (!Files.isDirectory(pathToFile)) {
                context = Files.readAllBytes(pathToFile);
                statusCode = 200;
            }
            else {
                // Это директория. 403 or 404?
                context = null;
                statusCode = 403;
            }
        }
        else {
            context = null;
            statusCode = 404;
        }
    }

    private String getResponseHeader() {
        StringBuilder headers = new StringBuilder();

        headers.append(
                "HTTP/1.1 " + statusCode + " OK\r\n" +
                "Server: LarionovServer\r\n" +
                "Content-Type: text/html\r\n");

        if (context != null)
            headers.append("Content-Length: " + context.length + "\r\n");

        headers.append(
                "Connection: close\r\n" +
                "Date: " + (new Date()) + "\r\n\r\n");

        return headers.toString();
    }

    public ByteBuf getResponse() {
        // Проверка Path

        // Формируем context

        // Формируем headers
        String headers = getResponseHeader();

        // Формируем response и возвращаем
        ByteBuf response;
        System.err.println("Status code: " + statusCode);
        if (statusCode == 200) {
            response = Unpooled.copiedBuffer(headers.getBytes(), context);
        }
        else {
            response = Unpooled.copiedBuffer(headers.getBytes());
        }
        response.retain();

        return response;
    }
}
