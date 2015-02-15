import java.io.*;
import java.net.Socket;

/**
 * Created by Vadim on 15.02.15.
 */
public class SocketManager implements Runnable {
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    SocketManager(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
    }

    public void run() {
        System.out.println("Method run()");
        try {
            readInputHeaders();
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

    private void readInputHeaders() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        while (true) {
            String header = bufferedReader.readLine();
            if (header == null || header.trim().length() == 0) {
                break;
            }
        }
    }

    private void writeResponse(String message) throws IOException {
        String headers = "HTTP/1.1 OK\r\n" +
                "Server: LarionovServer\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + message.length() + "\r\n" +
                "Connection: close\r\n\r\n";
        String response = headers + message;
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

}
