import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;


/**
 * Created by Vadim on 15.02.15.
 */
public class SocketManager implements Runnable {
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    private static final String ROOT = "/home/vadim";

    SocketManager(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
    }

    public void run() {
        try {
            String request = readInputHeaders();

            String requestPath = getRequestPath(request);
            System.out.println("Request Path: " + requestPath);

            if (requestPath.equals("/"))
                requestPath = "/index.html";

            File file = new File(ROOT, requestPath);
            if (!file.exists()) {
                System.err.println("FUUUU! " + ROOT + requestPath);
                requestPath = "/404.html";
                file = new File(ROOT, requestPath);
            }

            writeResponse(file, requestPath);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.err.println("Client processing finished");
    }

    private String readInputHeaders() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String request = new String();
        while (true) {
            String header = bufferedReader.readLine();
            request += header;
            if (header == null || header.trim().length() == 0) {
                break;
            }
        }
        return request;
    }

    private void writeResponse(File context, String requestPath) throws IOException {
        String headers = makeHeaders(200, context.length());
        String response = headers;

        outputStream.write(response.getBytes());
        Path path = context.toPath();
        byte[] data = Files.readAllBytes(path);
        outputStream.write(data);
        outputStream.flush();
    }

    private String getRequestMethod(String request) throws UnsupportedEncodingException {
        int endIndex = request.indexOf(" ");
        return  (endIndex != 0) ? request.substring(0, endIndex) : null;
    }

    private String getRequestPath(String request) {
        int beginPath = request.indexOf(" ") + 1;
        int endPath = request.indexOf(" ", beginPath);
        // Какие могут быть ошибки?

        String path = request.substring(beginPath, endPath);
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        int queryIndex = path.indexOf("?");
        if (queryIndex != -1)
            path = path.substring(0, queryIndex);

        return path;
    }


    private String makeHeaders(int statusCode, long contextLength) {
        Date date = new Date();
        String headers = "HTTP/1.1 " + statusCode + " OK\r\n" +
                "Server: LarionovServer\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + contextLength + "\r\n" +
                "Connection: close\r\n" +
                "Date: " + date + "\r\n\r\n";

        return headers;
    }

}
