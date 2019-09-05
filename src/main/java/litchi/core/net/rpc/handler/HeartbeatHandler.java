//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.rpc.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import litchi.core.Litchi;
import litchi.core.net.rpc.client.NettyRpcClient;
import litchi.core.net.rpc.packet.HeartbeatPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 0x737263
 */
@Sharable
public class HeartbeatHandler extends BaseChannelHandler<HeartbeatPacket> {
    private static Logger LOGGER = LoggerFactory.getLogger(HeartbeatHandler.class);

    Litchi litchi;
    NettyRpcClient client;

    /**
     * use by server
     */
    public HeartbeatHandler() {
        super(HeartbeatPacket.class);
    }

//    /**
//     * use by client
//     * @param litchi
//     * @param client
//     */
//    public HeartbeatHandler(litchi litchi, NettyRpcClient client) {
//        super(HeartbeatPacket.class);
//        this.litchi = litchi;
//        this.client = client;
//
//        litchi.schedule().addEverySecond(() -> {
//            if (client.isConnect()) {
//                HeartbeatPacket packet = new HeartbeatPacket(litchi.currentNode().getNodeType(), litchi.currentNode().getNodeId());
//                client.writeRpcPacket(packet);
//            }
//        }, 30);
//    }

    @Override
    protected void onChannelRead(ChannelHandlerContext ctx, HeartbeatPacket packet) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("receive heartbeat from ... {}", packet);
        }
    }
}
