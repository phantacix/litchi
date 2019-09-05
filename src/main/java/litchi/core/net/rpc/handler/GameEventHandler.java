//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.rpc.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import litchi.core.Litchi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import litchi.core.event.GameEvent;

/**
 * GameEvent msg receive handler
 *
 * @author 0x737263
 */
@Sharable
public class GameEventHandler extends BaseChannelHandler<GameEvent> {
    private static Logger LOGGER = LoggerFactory.getLogger(GameEventHandler.class);

    Litchi litchi;

    public GameEventHandler(Litchi litchi) {
        super(GameEvent.class);
        this.litchi = litchi;
    }

    @Override
    protected void onChannelRead(ChannelHandlerContext ctx, GameEvent packet) {
        if(LOGGER.isDebugEnabled()) {
            LOGGER.info("<---------- GameEvent  {}", packet);
        }
        litchi.event().post(packet);
    }
}
