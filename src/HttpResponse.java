import java.util.Date;

/**
 * Created by vadim on 23.02.15.
 */
public class HttpResponse {

    public String getResponseHeader(int responseCode, long contextLength) {
        Date date = new Date();
        String headers = "HTTP/1.1 " + responseCode + " OK\r\n" +
                "Server: LarionovServer\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + contextLength + "\r\n" +
                "Connection: close\r\n" +
                "Date: " + date + "\r\n\r\n";
        return headers;
    }
}
