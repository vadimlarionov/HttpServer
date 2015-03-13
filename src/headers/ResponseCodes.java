package headers;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by vadim on 25.02.15.
 */
public class ResponseCodes {
    private static ConcurrentHashMap<Integer, String> responseCodeMap;

    public static final int OK = 200;

    public static final int BAD_REQUEST = 400;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int METHOD_NOT_ALLOWED = 405;


    static {
        initResponseCodeMap();
    }
    public ResponseCodes() {}

    private static void initResponseCodeMap() {
        responseCodeMap = new ConcurrentHashMap<>();

        // 2xx
        responseCodeMap.put(OK, "OK");

        // 4xx
        responseCodeMap.put(BAD_REQUEST, "Bad Request");
        responseCodeMap.put(FORBIDDEN, "Forbidden");
        responseCodeMap.put(NOT_FOUND, "Not Found");
        responseCodeMap.put(METHOD_NOT_ALLOWED, "Method Not Allowed");
    }

    public static String getResponseCodeValue(int code) {
        String responseCode = responseCodeMap.get(code);
        return responseCode != null ? responseCode : "";
    }
}