//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.session;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import litchi.core.common.StatusCode;
import litchi.core.net.rpc.packet.RequestPacket;
import litchi.core.net.rpc.packet.ResponsePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public void writeToClient(RequestPacket packet, byte[] data) {
        writeToClient(packet.toResponsePacket(data));
    }

    public void writeToClient(ResponsePacket responsePacket) {
        super.writeAndFlush(responsePacket);
    }

}