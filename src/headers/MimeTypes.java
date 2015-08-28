package headers;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by vadim on 06.03.15.
 */
public class MimeTypes {
    private static ConcurrentHashMap<String, String> mimeTypeMap;
    private static final String defaultMimeType = "text/html";

    static {
        initMimeTypeMap();
    }

    private MimeTypes() {}

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
        mimeTypeMap.put("avi", "video/avi");

        // Application
        mimeTypeMap.put("json", "application/json");
        mimeTypeMap.put("swf", "application/x-shockwave-flash");
        mimeTypeMap.put("pdf", "application/pdf");
        mimeTypeMap.put("iso", "application/x-zip-compressed");
    }

    public static String getDefaultMimeType() {
        return defaultMimeType;
    }

    public static String getMimeType(String fileType) {
        return mimeTypeMap.getOrDefault(fileType, defaultMimeType);
    }
}
