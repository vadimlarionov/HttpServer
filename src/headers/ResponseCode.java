package headers;

/**
 * Created by vadim on 22.08.15.
 */
public class ResponseCode {
    private int code;
    private String codeTitle;

    public ResponseCode(int code, String codeTitle) {
        if (code < 0)
            throw new IllegalArgumentException("code: " + code + " (expected code > 0");
        this.code = code;
        this.codeTitle = codeTitle;
    }

    public int getCode() {
        return code;
    }

    public String getCodeTitle() {
        return codeTitle;
    }
}
