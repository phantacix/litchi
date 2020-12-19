package litchi.core.common.logback.property;

import ch.qos.logback.core.PropertyDefinerBase;
import litchi.core.Litchi;

public class NodeIdProperty extends PropertyDefinerBase {

    @Override
    public String getPropertyValue() {
        return Litchi.call().getNodeId();
    }
}