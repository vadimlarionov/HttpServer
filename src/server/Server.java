package server;

import handlers.HttpRequestDecoder;
import handlers.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.cli.*;

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
            .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
            //.option(ChannelOption.SO_BACKLOG, 256)          // Максимальное кол-во requests в очереди
            .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            // Start the server
            ChannelFuture f = b.bind(inetHost, port).sync();

            // Ожидать, пока сокета сервера не будет закрыт
            f.channel().closeFuture().sync();
        }
        finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        int port = 80;
        String documentRoot = null;

        Options posixOptions = getPosixOptions();
        try {
            CommandLineParser cmdLinePosixParser = new PosixParser();
            CommandLine cmdLine = cmdLinePosixParser.parse(posixOptions, args);

            if (cmdLine.hasOption("host")) {
                host = cmdLine.getOptionValue("host");
            }

            if (cmdLine.hasOption("port")) {
                port = Integer.valueOf(cmdLine.getOptionValue("port"));
            }

            if (cmdLine.hasOption("rootdir")) {
                documentRoot = cmdLine.getOptionValue("rootdir");
            }
        }
        catch (Exception e) {
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("java -jar httpd.jar -r <document root> <options>", posixOptions);
            return;
        }

        Settings.setInetHost(host);
        Settings.setPort(port);
        Settings.setDocumentRoot(documentRoot);

        System.out.println("LarionovServer has been started at " + host + ":" + port);
        new Server(Settings.getInetHost(), Settings.getPort()).run();
    }

    private static Options getPosixOptions() {
        Option hostOption = new Option("b", "host", true, "Host");
        hostOption.setArgs(1);
        hostOption.setOptionalArg(true);
        hostOption.setArgName("host");

        Option portOption = new Option("p", "port", true, "Port");
        portOption.setArgs(1);
        portOption.setOptionalArg(true);
        portOption.setArgName("port");

        Option rootDirOption = new Option("r", "rootdir", true, "Document Root");
        rootDirOption.setArgs(1);
        rootDirOption.setRequired(true);
        rootDirOption.setArgName("DocumentRoot");

        Options posixOptions = new Options();
        posixOptions.addOption(hostOption);
        posixOptions.addOption(portOption);
        posixOptions.addOption(rootDirOption);

        return posixOptions;
    }

}