//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.session;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import litchi.core.exception.ErrorCodeException;
import litchi.core.net.rpc.packet.RequestPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import litchi.core.common.StatusCode;

public class GateSession extends NettySession implements StatusCode {
    protected Logger LOGGER = LoggerFactory.getLogger(GateSession.class);

    public static AttributeKey<String> NODE_ID = AttributeKey.valueOf("netty.channel.node-id");

    public GateSession(Channel channel) {
        super(channel);
    }

    public String getChannelId() {
        return channel().id().asShortText();
    }

    public void close() {
        this.channel().close();
    }

    public String getNodeId() {
        Attribute<String> attr = channel().attr(NODE_ID);
        return attr.get() == null ? null : attr.get();
    }

    public void setNodeId(String nodeID) {
        Attribute<String> attr = channel().attr(NODE_ID);
        attr.set(nodeID);
    }

    public void returnError(short statusCode) {
        throw new ErrorCodeException(statusCode);
    }

    public void returnError(RequestPacket request, short statusCode) {
        //session.write(packet.getMessageId(), packet.getRoute(), statusCode, null);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("error : code={}, RequestPacket = {}", statusCode, request);
        }
        throw new ErrorCodeException(statusCode);
    }

    public void returnResponse(RequestPacket request, Message response) {
        this.write(request.messageId, request.route, StatusCode.SUCCESS, response.toByteArray());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("response: uid={}, RpcRequest = {}, Response = {} \n", uid(), request, response);
        }
    }

    public void write(short messageId, String route, short statusCode, byte[] data) {
        ByteBuf buffer = build(messageId, route, statusCode, data);
        writeAndFlush(new BinaryWebSocketFrame(buffer));
    }

    public void write(String route, short statusCode) {
        write((short) 0, route, statusCode, null);
    }

    public void write(short messageId, String route, short statusCode) {
        write(messageId, route, statusCode, null);
    }

}