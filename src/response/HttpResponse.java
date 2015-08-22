package response;

import headers.Headers;
import headers.ResponseCode;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;


/**
 * Created by vadim on 07.03.15.
 */
public class HttpResponse {
    private ResponseCode responseCode;
    private Headers headers;
    private byte[] context = null;

    public HttpResponse(ResponseCode responseCode) {
        this.responseCode = responseCode;
        headers = new Headers(responseCode.getCode(), responseCode.getCodeTitle());
    }

    public void setContext(byte[] context) {
        this.context = context;
    }

    public void setHeader(String key, String value) {
        headers.setHeader(key, value);
    }

    public ByteBuf toByteBuf() {
        byte[] separator = {'\r', '\n'};
        int capacity = headers.toString().length() + separator.length;
        PooledByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;
        ByteBuf response;
        if (context != null) {
            capacity += context.length;
            response = allocator.directBuffer(capacity);
            response.writeBytes(headers.toString().getBytes());
            response.writeBytes(separator);
            response.writeBytes(context);
        }
        else {
            response = allocator.directBuffer(capacity);
            response.writeBytes(headers.toString().getBytes());
            response.writeBytes(separator);
        }

        return response;
    }

    public byte[] getContext() {
        return context;
    }

    public Headers getHeaders() {
        return headers;
    }
}
