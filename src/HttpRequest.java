/**
 * Created by vadim on 07.03.15.
 */
public class HttpRequest {
    private String method;
    private String uri;

    public HttpRequest(String method, String uri) {
        this.method = method.toUpperCase();
        this.uri = uri;
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }
}
