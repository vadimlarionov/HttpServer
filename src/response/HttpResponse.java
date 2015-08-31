package response;

import headers.Headers;
import headers.ResponseCode;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;


/**
 * Created by vadim on 07.03.15.
 */
public class HttpResponse {
    private Headers header;
    private byte[] context = null;
    private static final byte[] separator = {'\r', '\n'};

    public HttpResponse(ResponseCode responseCode) {
        header = new Headers(responseCode.getCode(), responseCode.getCodeTitle());
    }

    public byte[] getContext() {
        return context;
    }

    public void setContext(byte[] context) {
        this.context = context;
    }

    public Headers getHeader() {
        return header;
    }

    public ByteBuf toByteBuf() {
        byte[] headerBytes = header.getHeaders();
        int capacity = headerBytes.length + separator.length;
        PooledByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;
        ByteBuf response;
        if (context == null) {
            response = allocator.directBuffer(capacity);
            response.writeBytes(headerBytes);
            response.writeBytes(separator);
        }
        else {
            capacity += context.length;
            response = allocator.directBuffer(capacity);
            response.writeBytes(headerBytes);
            response.writeBytes(separator);
            response.writeBytes(context);
        }

        return response;
    }
}
