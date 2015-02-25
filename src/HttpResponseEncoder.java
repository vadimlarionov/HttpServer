import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;


/**
 * Created by vadim on 21.02.15.
 */
public class HttpResponseEncoder extends ChannelOutboundHandlerAdapter {
    public HttpResponseEncoder() {}

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("Yo! WRITE");

        //ctx.write(message, promise);
        super.write(ctx, msg, promise);
    }


}
