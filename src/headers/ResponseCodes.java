package headers;

/**
 * Created by vadim on 25.02.15.
 */
public class ResponseCodes {
    // 2xx
    public static final ResponseCode OK = new ResponseCode(200, "OK");

    // 4xx
    public static final ResponseCode BAD_REQUEST = new ResponseCode(400, "Bad Request");
    public static final ResponseCode FORBIDDEN = new ResponseCode(403, "Forbidden");
    public static final ResponseCode NOT_FOUND = new ResponseCode(404, "Not Found");
    public static final ResponseCode METHOD_NOT_ALLOWED = new ResponseCode(405, "Method Not Allowed");

    // 5xx
    public static final ResponseCode INTERNAL_SERVER_ERROR = new ResponseCode(500, "Internal Server Error");

}