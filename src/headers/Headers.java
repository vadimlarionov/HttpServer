package headers;

import io.netty.handler.codec.http.HttpHeaders;
import javafx.util.Pair;

import java.util.Date;
import java.util.Map;

/**
 * Created by vadim on 07.03.15.
 */
public class Headers{
    StringBuilder headers;

    public Headers(int responseCode, String responseCodeTitle) {
        headers = new StringBuilder(1024);
        headers
                .append("HTTP/1.1 ")
                .append(responseCode).append(" ")
                .append(responseCodeTitle).append("\r\n")
                .append("Connection: close\r\n")
                .append("Server: LarionovServer\r\n")
                .append("Date: ").append(new Date()).append("\r\n");
    }

    public void setContentType(String contentType) {
        headers.append("Content-Type: ").append(contentType).append("\r\n");
    }

    public void setContentLength(long contentLength) {
        headers.append("Content-Length: ").append(contentLength).append("\r\n");
    }

    public void setHeader(String key, String value) {
        headers.append(key).append(": ").append(value).append("\r\n");
    }

    @Override
    public String toString() {
        return headers.toString();
    }

}
