//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.websocket;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.timeout.IdleStateHandler;
import litchi.core.Litchi;
import litchi.core.exception.CoreException;

public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {
    private static final String WEB_SOCKET_PATH = "/";

    private SslContext sslCtx;
    private ChannelHandler[] handlers;

    Litchi litchi;

    public WebSocketServerInitializer(Litchi litchi, boolean openSSL, ChannelHandler... handlers) {
        this.litchi = litchi;
        if (openSSL) {
            this.sslCtx = createSSLContext();
        }
        this.handlers = handlers;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerCompressionHandler());
        pipeline.addLast(new IdleStateHandler(0, 0, 60));
        pipeline.addLast(new WebSocketServerProtocolHandler(WEB_SOCKET_PATH, null, true));
        pipeline.addLast(new WebSocketHandler(litchi));
    }

    private SslContext createSSLContext() {
        try {
            // Configure SSL.
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            // 如需开启wss，在此处配置证书文件路径
            // SslContextBuilder.forServer(keyCertChainInputStream, keyInputStream)
            SslContext sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
            return sslCtx;
        } catch (Exception ex) {
            throw new CoreException("ssl context create fail. ", ex);
        }
    }
}