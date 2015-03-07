import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Date;

/**
 * Created by vadim on 07.03.15.
 */
public class HttpResponseError {
    private int responseCode;
    private String responseCodeMsg;
    private Headers headers;

    public HttpResponseError(int responseCode) {
        this.responseCode = responseCode;
        this.responseCodeMsg = ResponseCodes.getResponseCodeValue(responseCode);
        headers = new Headers(responseCode, responseCodeMsg);
    }

    public ByteBuf getResponse() {
        StringBuilder buf = new StringBuilder();
        buf.append("\r\n");
        buf.append("<html><body><h3>");
        buf.append(responseCode);
        buf.append(". ").append(responseCodeMsg);
        buf.append("</h3></body></html>");
        byte[] context = buf.toString().getBytes();

        headers.setHeader("Server", "LarionovServer");
        headers.setHeader("Content-Type", "text/html");
        headers.setHeader("Content-Length", String.valueOf(context.length));
        headers.setHeader("Connection", "close");
        headers.setHeader("Date: ", (new Date()).toString());

        ByteBuf response = Unpooled.copiedBuffer(headers.toString().getBytes(), context);

        return response;
    }

    public void setHeader(String key, String value) {
        headers.setHeader(key, value);
    }

}
