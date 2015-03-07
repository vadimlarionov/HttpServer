import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Created by vadim on 07.03.15.
 */
public class HttpResponse {
    private int responseCode;
    private String responseCodeMsg;
    private Headers headers;

    private byte[] context = null;

    public HttpResponse(int responseCode) {
        this.responseCode = responseCode;
        responseCodeMsg = ResponseCodes.getResponseCodeValue(responseCode);
        headers = new Headers(responseCode, responseCodeMsg);
    }

    public void setHeader(String key, String value) {
        headers.setHeader(key, value);
    }


    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseCodeMsg() {
        return responseCodeMsg;
    }


    public void setContext(byte[] context) {
        this.context = context;
    }

    public ByteBuf getResponse() {
        byte[] separator = "\r\n".getBytes();
        ByteBuf response;
        if (context != null) {
            response = Unpooled.copiedBuffer(headers.toString().getBytes(), separator, context);
        }
        // Для else нужен separator?
        else {
            response = Unpooled.copiedBuffer(headers.toString().getBytes(), separator);
        }

        return response;
    }

}
