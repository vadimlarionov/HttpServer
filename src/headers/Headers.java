package headers;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vadim on 07.03.15.
 */
public class Headers {
    private int responseCode;
    private String responseCodeTitle;
    private HashMap<String, String> headersMap;

    public Headers(int responseCode, String responseCodeTitle) {
        this.responseCode = responseCode;
        this.responseCodeTitle = responseCodeTitle;
        headersMap = new HashMap<>();
    }

    public void setDefaultHeaders() {
        headersMap.put("Server", "LarionovServer");
        headersMap.put("Date", (new Date()).toString());
        headersMap.put("Connection", "close");
    }

    public void setHeader(String key, String value) {
        headersMap.put(key, value);
    }

    public byte[] getHeaders() {
        StringBuilder builder = new StringBuilder(128);
        builder.append("HTTP/1.1 ").append(responseCode).append(" ")
                .append(responseCodeTitle).append("\r\n");
        for (Map.Entry<String, String> entry : headersMap.entrySet()) {
            builder.append(entry.getKey()).append(": ")
                    .append(entry.getValue()).append("\r\n");
        }
        return String.valueOf(builder).getBytes();
    }
}
