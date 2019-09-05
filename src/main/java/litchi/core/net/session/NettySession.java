//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.session;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import litchi.core.common.utils.CRCUtils;
import litchi.core.common.utils.StringUtils;
import litchi.core.net.rpc.packet.RpcPacket;

import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicLong;

public class NettySession {
    private static AtomicLong atomicId = new AtomicLong(1);

    public static AttributeKey<Long> SESSION_ID = AttributeKey.valueOf("netty.session.id");
    public static AttributeKey<String> NETTY_REMOTE_IP_KEY = AttributeKey.valueOf("netty.channel.remote.ip");

    public static final AttributeKey<String> FROM_NODE_TYPE = AttributeKey.valueOf("from.node.type");
    public static final AttributeKey<String> FROM_NODE_ID = AttributeKey.valueOf("from.node.id");

    public static AttributeKey<Long> UID = AttributeKey.valueOf("user.uid");

    private Channel channel;

    public NettySession(Channel channel) {
        Attribute<Long> attrId = channel.attr(SESSION_ID);
        attrId.set(atomicId.incrementAndGet());
        this.channel = channel;
    }

    public long getSessionId() {
        Attribute<Long> attrId = channel.attr(SESSION_ID);
        return attrId.get();
    }

    public void setUid(long uid) {
        Attribute<Long> attribute = channel().attr(UID);
        attribute.set(uid);
    }

    public long uid() {
        Attribute<Long> attribute = channel().attr(UID);
        return attribute.get() == null ? 0L : attribute.get();
    }

    public void close() {
        this.channel.close();
    }

    public Channel channel() {
        return channel;
    }

    public <T> Attribute<T> attr(AttributeKey<T> key) {
        return channel.attr(key);
    }

    public String getRemoteIp() {
        Attribute<String> ipKey = this.channel.attr(NETTY_REMOTE_IP_KEY);
        if (StringUtils.isNotBlank(ipKey.get())) {
            return ipKey.get();
        }

        SocketAddress add = this.channel.remoteAddress();
        if (add != null) {
            ipKey.set(ChannelUtils.getRemoteIp(this.channel));
        }
        return ipKey.get();
    }

    public ByteBuf build(short messageId, String route, short statusCode, byte[] data) {
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeShort(messageId);
        buffer.writeByte(route.length());
        buffer.writeBytes(route.getBytes());
        buffer.writeShort(statusCode);

        if (data != null) {
            buffer.writeShort(data.length);
            buffer.writeBytes(data);
            byte[] crcBytes = new byte[6 + data.length];
            buffer.getBytes(0, crcBytes);
            long crc = CRCUtils.calculateCRC(CRCUtils.Parameters.CRC32, crcBytes);
            buffer.writeLong(crc);
        }
        return buffer;
    }

    public void writeAndFlush(Object obj) {
        channel.writeAndFlush(obj);
    }

    public void writeRpcPacket(Object obj) {
        channel.writeAndFlush(new RpcPacket<>(obj));
    }


//    public void write(short messageId, String route, short statusCode, byte[] data) {
//        ByteBuf buffer = Unpooled.buffer();
//        buffer.writeShort(messageId);
//        buffer.writeByte(route.length());
//        buffer.writeBytes(route.getBytes());
//        buffer.writeShort(statusCode);
//
//        if (data != null) {
//            buffer.writeShort(data.length);
//            buffer.writeBytes(data);
//            byte[] crcBytes = new byte[6 + data.length];
//            buffer.getBytes(0, crcBytes);
//            long crc = CRCUtils.calculateCRC(Parameters.CRC32, crcBytes);
//            buffer.writeLong(crc);
//        }
//
//        write(buffer);
//    }

    @Override
    public String toString() {
        return String.format("Session=[sessionId = %s, uid = %s, nodeType = %s, serverId = %s]",
                getSessionId(), uid(), attr(FROM_NODE_TYPE), attr(FROM_NODE_ID));
    }
}
