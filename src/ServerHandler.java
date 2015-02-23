import io.netty.buffer.ByteBuf;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;

/**
 * Handles a server-side channel.
 */
public class ServerHandler extends SimpleChannelInboundHandler<Object> { // (1)

    private static final String LOG_TAG = ServerHandler.class.getName();
//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) {
//        ctx.flush();
//    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) { // (2)
        System.out.println(LOG_TAG + " channelRead0()");
        //System.out.println((String) msg);

        //byte[] bytes = ((String) msg).getBytes();
        //ByteBuf byteBuf = Unpooled.copiedBuffer(bytes);
        //ctx.writeAndFlush(byteBuf).addListener(ChannelFutureListener.CLOSE);

        ctx.writeAndFlush(msg).addListener(ChannelFutureListener.CLOSE);

//        // Discard the received data silently.
//        ByteBuf in = (ByteBuf) msg;
//        System.out.println(ctx);
//        //ctx.write(msg);
//        try {
//            while (in.isReadable()) {
//                System.out.print((char) in.readByte());
//                System.out.flush();
//            }
//
//            System.out.println("Check1");
//        }
//        finally {
//            HttpResponse response = new HttpResponse();
//            String context = "<html><body>Heelo</body></html>";
//            String ret = response.getResponseHeader(200, context.length());
//            ret += context;
//            System.out.println("RET");
//            System.out.println(ret);
//
//            ctx.writeAndFlush(ret).addListener(ChannelFutureListener.CLOSE);
//            System.out.println("Check2");
//
//            //ReferenceCountUtil.release(msg);
//            System.out.println("Check3");
//        }
//        //((ByteBuf) msg).release(); // (3)
//
//        System.out.println("Check4");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}