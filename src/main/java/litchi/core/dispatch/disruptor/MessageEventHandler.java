//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.dispatch.disruptor;

import com.lmax.disruptor.EventHandler;
import litchi.core.Litchi;

/**
 * @author 0x737263
 */
public class MessageEventHandler implements EventHandler<MessageBuffer> {

    Litchi litchi;

    public MessageEventHandler(Litchi litchi) {
        this.litchi = litchi;
    }

    @Override
    public void onEvent(MessageBuffer buffer, long sequence, boolean endOfBatch) throws Exception {
        try {
            buffer.execute();

        } catch (Exception ex) {
            throw ex;
        } finally {
            buffer.clear();
        }
    }
}
