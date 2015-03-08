import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by vadim on 15.02.15.
 */
public class Server {
    private String inetHost;
    private int port;

    public Server(String inetHost, int port) {
        this.inetHost = inetHost;
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
                     channel.pipeline()
                             .addLast(new HttpRequestDecoder())
                             .addLast(new ServerHandler());
                 }
             })
             .option(ChannelOption.SO_BACKLOG, 128)
             .childOption(ChannelOption.SO_KEEPALIVE, false); // Or true?


            ChannelFuture f = b.bind(inetHost, port).sync();
            f.channel().closeFuture().sync();
        }
        finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        String inetHost = "127.0.0.1";
        int port = 8030;
        String documentRoot = "/home/vadim/http-test-suite";

        Settings.setInetHost(inetHost);
        Settings.setPort(port);
        Settings.setDocumentRoot(documentRoot);

        new Server(Settings.getInetHost(), Settings.getPort()).run();
    }
}