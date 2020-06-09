//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import litchi.core.Litchi;
import litchi.core.common.StatusCode;
import litchi.core.common.utils.SnappyHelper;

public class GateSession extends NettySession implements StatusCode {
    protected Logger LOGGER = LoggerFactory.getLogger(GateSession.class);

    public static AttributeKey<String> NODE_ID = AttributeKey.valueOf("netty.channel.node-id");

    public GateSession(Channel channel) {
        super(channel);
    }

    public String getChannelId() {
        return channel().id().asLongText();
    }

    public void close() {
        this.channel().close();
    }

    public String getNodeId() {
        Attribute<String> attr = channel().attr(NODE_ID);
        return attr.get() == null ? null : attr.get();
    }

    public void setNodeId(String nodeId) {
        Attribute<String> attr = channel().attr(NODE_ID);
        attr.set(nodeId);
    }

    public void writeWebSocketFrame(short messageId, String route, short statusCode, byte[] data) {
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeShort(messageId);
        buffer.writeByte(route.length());
        buffer.writeBytes(route.getBytes());
        buffer.writeShort(statusCode);

        if (data != null) {
        	int compress = 0;
        	byte[] sendData = data;
        	Integer protoRespCompressSize = Litchi.call().config().getInteger("protoRespCompressSize");
        	if (protoRespCompressSize == null) {
        		protoRespCompressSize = 512;
			}
        	
        	if (data.length > protoRespCompressSize) {
        		compress = 1;
        		sendData = SnappyHelper.compress(data);
			}
        	buffer.writeByte(compress);
            buffer.writeShort(sendData.length);
            buffer.writeBytes(sendData);
        }
        super.writeAndFlush(new BinaryWebSocketFrame(buffer));
    }

    public void writeWebSocketFrame(short messageId, String route, short statusCode) {
        writeWebSocketFrame(messageId, route, statusCode, null);
    }
}