package litchi.core.net.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import litchi.core.common.utils.CRCUtils;
import litchi.core.net.rpc.packet.ResponsePacket;

import java.util.List;


// ----------------------------------------------------------------------------------
// 服务端[响应]包结构解析 ResponsePacket.java
// messageId	short       2       客户端的透传消息id(原路返回)
// routeLen     byte        1       路由名称长度
// route        byte[]      n       路由名称
// statusCode   short       2       状态码（详情:StatusCode.java)，如果不为0则没有响应的消息数据
// dataLen      short       2       响应的消息数据长度
// data         byte[]      n       响应的消息数据(结构可使用json、pb等第三方序列库)
// ----------------------------------------------------------------------------------
@Sharable
public class WebSocketEncoder extends MessageToMessageEncoder<ResponsePacket> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ResponsePacket packet, List<Object> out) {
        ByteBuf buffer = encode(packet);
        out.add(new BinaryWebSocketFrame(buffer));
    }

    public ByteBuf encode(ResponsePacket packet) {
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeShort(packet.messageId);
        buffer.writeByte(packet.route.length());
        buffer.writeBytes(packet.route.getBytes());
        buffer.writeShort(packet.statusCode);
        if (packet.data != null) {
            buffer.writeShort(packet.data.length);
            buffer.writeBytes(packet.data);
            byte[] crcBytes = new byte[6 + packet.data.length];
            buffer.getBytes(0, crcBytes);
            long crc = CRCUtils.calculateCRC(CRCUtils.Parameters.CRC32, crcBytes);
            buffer.writeLong(crc);
        }
        return buffer;
    }

}