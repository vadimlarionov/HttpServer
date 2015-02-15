import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.*;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * Created by Vadim on 15.02.15.
 */
public class SocketManager implements Runnable {
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    private int bufferSize = 8192;

    SocketManager(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
    }

    public void run() {
        System.out.println("Method run()");
        try {
            String request = readInputHeaders();

            String requestMethod = getRequestMethod(request);
            System.out.println("Request Method: " + requestMethod);

            String requestPath = getRequestPath(request);
            System.out.println("Request Path: " + requestPath);

            String str = "<html><body><h1>Simple Http Server</h1></body></html>\n";
            writeResponse(str);
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
            System.out.println(header);
            if (header == null || header.trim().length() == 0) {
                break;
            }
        }
        return request;
    }

    private void writeResponse(String message) throws IOException {
        String headers = "HTTP/1.1 200 OK\r\n" +
                "Server: LarionovServer\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + message.length() + "\r\n" +
                "Connection: close\r\n\r\n";
        String response = headers + message;
        outputStream.write(response.getBytes());
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
        System.out.println(queryIndex);
        if (queryIndex != -1)
            path = path.substring(0, queryIndex);

        return path;
    }

}
