
import io.netty.bootstrap.ServerBootstrap;

import io.netty.channel.*;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;


public class Server {
    private int port;

    public Server(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        System.out.println("Netty Web Server");
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel channel) throws Exception {
                     channel.pipeline().addLast(new HttpRequestDecoder())
                             .addLast(new HttpResponseEncoder())
                             .addLast(new ServerHandler());
                 }
             })
             .option(ChannelOption.SO_BACKLOG, 128)
             .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        }
        finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8030;
        new Server(port).run();
    }
}










//import java.io.IOException;
//import java.net.ServerSocket;
//import java.net.Socket;
//
//
//public class Server {
//
//    public static void main(String[] args) throws IOException {
//        System.out.println("Simple Http Server!");
//
//        int port = 8030;
//        ServerSocket serverSocket = new ServerSocket(port);
//
//        while (true) {
//            Socket socket = serverSocket.accept();
//            System.err.println("Client accepted");
//            new Thread(new SocketManager(socket)).start();
//        }
//
//    }
//}
