//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import litchi.core.Constants;
import litchi.core.Litchi;
import litchi.core.components.NetComponent;
import litchi.core.exception.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class WebSocketServer extends NetComponent {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketServer.class);

    Litchi litchi;
    private int port;

    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private ChannelInitializer<SocketChannel> channelInitializer;

    public WebSocketServer(Litchi litchi) {
        this(litchi, new WebSocketServerInitializer(litchi));
    }

    public WebSocketServer(Litchi litchi, SslContext sslContext) {
        this(litchi, new WebSocketServerInitializer(litchi, sslContext));
    }

    public WebSocketServer(Litchi litchi, ChannelInitializer<SocketChannel> channelInitializer) {
        this.litchi = litchi;
        this.port = litchi.currentNode().getPort();
        this.channelInitializer = channelInitializer;
    }

    @Override
    public void start() {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(channelInitializer);

            Channel ch = bootstrap.bind(port).sync().channel();
            LOGGER.info(" ------> url : ws://127.0.0.1:{}", port);
            new Thread(() -> {
                try {
                    ch.closeFuture().sync();
                } catch (InterruptedException e) {
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                }
            }).start();
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    @Override
    public String name() {
        return Constants.Net.WEB_SOCKET_SERVER;
    }

    @Override
    public void afterStart() {

    }

    /**
     * @param filePath *.pem file
     * @param keyPath  *.key file
     * @return
     */
    public static SslContext createSSLContext(String filePath, String keyPath) {
        try {
            File file = new File(filePath);
            File key = new File(keyPath);

            SslContext sslCtx = SslContextBuilder.forServer(file, key).build();
            return sslCtx;
        } catch (Exception ex) {
            throw new CoreException("ssl context create fail. ", ex);
        }
    }
}