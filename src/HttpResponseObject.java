import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

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
        return statusCode + " " + Constants.getResponseCodeValue(statusCode);
    }
    private String getMimeType() {
        String path = pathToFile.toString();
        String fileType = path.substring(path.indexOf(".") + 1).toLowerCase();
        System.out.println("fileType: " + fileType);
        String mimeType = Constants.getMimeType(fileType);
        return mimeType;
    }

    private int getContentLength() {
        return context != null ? context.length : 0;
    }

    public ByteBuf getResponse() {
        String headers = getResponseHeader();

        // Формируем response и возвращаем
        ByteBuf response;
        System.err.println("Headers");
        System.out.println(headers);

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
