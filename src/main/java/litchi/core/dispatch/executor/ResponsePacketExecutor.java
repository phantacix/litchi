//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.dispatch.executor;

import litchi.core.dispatch.disruptor.MessageThread;
import litchi.core.net.rpc.packet.ResponsePacket;
import litchi.core.net.session.NettySession;
import litchi.core.dispatch.disruptor.MessageThread;
import litchi.core.net.rpc.packet.ResponsePacket;
import litchi.core.net.session.NettySession;

import java.util.List;

/**
 * @author 0x737263
 */
public class ResponsePacketExecutor implements BaseExecutor {

    NettySession session;

    ResponsePacket rsp;

    public ResponsePacketExecutor(NettySession session, ResponsePacket rsp) {
        this.session = session;
        this.rsp = rsp;
    }

    @Override
    public int threadId() {
        return 0;
    }

    @Override
    public int calcHash(List<MessageThread> list) {
        return 0;
    }

    @Override
    public void invoke() {
        //session.write(rsp.messageId, rsp.route, rsp.statusCode, rsp.data);
    }
}
