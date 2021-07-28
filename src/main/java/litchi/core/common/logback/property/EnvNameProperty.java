package litchi.core.common.logback.property;

import ch.qos.logback.core.PropertyDefinerBase;
import litchi.core.Litchi;

public class EnvNameProperty extends PropertyDefinerBase {

    @Override
    public String getPropertyValue() {
        return Litchi.call().getEnvName();
    }
}
