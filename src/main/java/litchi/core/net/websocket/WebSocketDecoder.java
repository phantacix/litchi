package litchi.core.net.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import litchi.core.net.rpc.packet.RequestPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

// ----------------------------------------------------------------------------------
// 服务端[接收]包结构解析 RequestPacket.java
// messageId	short       2	 	客户端生成的透传消息id(用于客户端回调用)
// routeLen     byte        1       路由名称长度
// route        byte[]      n       路由名称
// bodyLen      short       2       请求的消息数据长度
// body         byte[]      n       请求的消息数据(结构可使用json、pb等第三方序列库)
// ----------------------------------------------------------------------------------
@Sharable
public class WebSocketDecoder extends MessageToMessageDecoder<WebSocketFrame> {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) {
        if (frame instanceof BinaryWebSocketFrame) {
            out.add(parse(frame.content()));
            return;
        }

        LOGGER.warn("unsupported frame type = {}, channelId={}", frame.getClass(), ctx.channel().id().asLongText());
    }

    private RequestPacket parse(ByteBuf buf) {
        RequestPacket request = new RequestPacket();
        request.messageId = buf.readShort();

        //route
        byte[] routeBytes = new byte[buf.readByte()];
        buf.readBytes(routeBytes);
        request.route = new String(routeBytes);

        //data
        byte[] data = new byte[buf.readShort()];
        buf.readBytes(data);
        request.args = new Object[1];
        request.args[0] = data;

        return request;
    }
}