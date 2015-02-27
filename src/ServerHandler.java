import io.netty.buffer.ByteBuf;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;

/**
 * Handles a server-side channel.
 */
public class ServerHandler extends SimpleChannelInboundHandler<Object> { // (1)

    private static final String LOG_TAG = ServerHandler.class.getName();

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) { // (2)
        System.out.println(LOG_TAG + " channelRead0()");

        ctx.writeAndFlush(msg).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}