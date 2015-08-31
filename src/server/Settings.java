package server;

/**
 * Created by vadim on 05.03.15.
 */
public class Settings {
    private static String inetHost = "127.0.0.1";
    private static int port = 80;
    private static String documentRoot = null;
    public static final String INDEX = "/index.html";

    private Settings() {}

    public static String getDocumentRoot() {
        return documentRoot;
    }
    public static int getPort() {
        return port;
    }
    public static String getInetHost() {
        return inetHost;
    }

    public static void setInetHost(String inetHost) {
        Settings.inetHost = inetHost;
    }
    public static void setPort(int port) {
        Settings.port = port;
    }
    public static void setDocumentRoot(String documentRoot) {
        Settings.documentRoot = documentRoot;
    }
}
