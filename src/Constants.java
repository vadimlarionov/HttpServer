import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by vadim on 25.02.15.
 */
public abstract class Constants {
    private static ConcurrentHashMap<Integer, String> responseCodeMap;
    private static ConcurrentHashMap<String, String> mimeTypeMap;

    static {
        initResponseCodeMap();
        initMimeTypeMap();
    }
    public Constants() {}

    private static void initResponseCodeMap() {
        responseCodeMap = new ConcurrentHashMap<>();
        responseCodeMap.put(200, "OK");
        responseCodeMap.put(400, "Bad Request");
        responseCodeMap.put(403, "Forbidden");
        responseCodeMap.put(404, "Not Found");
        responseCodeMap.put(405, "Method Not Allowed");
    }
    private static void initMimeTypeMap() {
        mimeTypeMap = new ConcurrentHashMap<>();

        // Text
        mimeTypeMap.put("css", "text/css");
        mimeTypeMap.put("csv", "text/csv");
        mimeTypeMap.put("html", "text/html");
        mimeTypeMap.put("txt", "text/txt");
        mimeTypeMap.put("js", "text/javascript");
        mimeTypeMap.put("php", "text/php");
        mimeTypeMap.put("xml", "text/xml");

        // Image
        mimeTypeMap.put("gif", "image/gif");
        mimeTypeMap.put("jpg", "image/jpeg");
        mimeTypeMap.put("jpeg", "image/jpeg");
        mimeTypeMap.put("pjpeg", "image/pjpeg");
        mimeTypeMap.put("png", "image/png");
        mimeTypeMap.put("ico", "image/x-icon");

        // Video
        mimeTypeMap.put("mpeg", "video/mpeg");
        mimeTypeMap.put("mp4", "video/mp4");
        mimeTypeMap.put("ogg", "video/ogg");
        mimeTypeMap.put("webm", "video/webm");
        mimeTypeMap.put("flv", "video/x-flv");

        // Application
        mimeTypeMap.put("json", "application/json");
        mimeTypeMap.put("swf", "application/x-shockwave-flash");
        mimeTypeMap.put("pdf", "application/pdf");
    }

    public static String getResponseCodeValue(int code) {
        String responseCode = responseCodeMap.get(code);
        return responseCode != null ? responseCode : "";
    }
    public static String getMimeType(String fileType) {
        String mimeType = mimeTypeMap.get(fileType);
        return mimeType != null ? mimeType : "text/html";
    }
}