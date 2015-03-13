package headers;

/**
 * Created by vadim on 07.03.15.
 */
public class Headers {
    StringBuilder headers;

    public Headers(int responseCode, String comment) {
        headers = new StringBuilder();
        headers
                .append("HTTP/1.1 ")
                .append(responseCode).append(" ")
                .append(comment).append("\r\n");
    }

    public void setHeader(String key, String value) {
        headers.append(key).append(": ").append(value).append("\r\n");
    }

    @Override
    public String toString() {
        return headers.toString();
    }

}
