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
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;
import litchi.core.Litchi;

public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {

    private SslContext sslCtx;
    private ChannelHandler[] handlers;

    Litchi litchi;

    public WebSocketServerInitializer(Litchi litchi, ChannelHandler... handlers) {
        this.litchi = litchi;
        this.handlers = handlers;
    }

    public WebSocketServerInitializer(Litchi litchi, SslContext sslCtx, ChannelHandler... handlers) {
        this.litchi = litchi;
        this.sslCtx = sslCtx;
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
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new WebSocketServerProtocolHandler("/", null, true));

        if (handlers != null) {
            pipeline.addLast(handlers);
        }
    }
}