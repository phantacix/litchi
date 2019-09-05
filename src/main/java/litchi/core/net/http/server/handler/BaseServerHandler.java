//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.http.server.handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

/**
 * @author 0x737263
 */
public abstract class BaseServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {


    protected void writeHttpStatus(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse rsp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
        rsp.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
        // 支持跨域
        rsp.headers().add("Access-Control-Allow-Origin", "*");
        rsp.headers().add("Access-Control-Allow-Methods", "POST");
        ctx.channel().writeAndFlush(rsp).addListener(ChannelFutureListener.CLOSE);
    }
}
