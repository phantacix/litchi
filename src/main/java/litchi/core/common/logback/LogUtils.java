//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.common.logback;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @author 0x737263
 */
public class LogUtils {

    public static void loadFileConfig(String configFilePath) throws IOException, JoranException {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        File externalConfigFile = new File(configFilePath);

        if (!externalConfigFile.isFile()) {
            throw new IOException("logback config file not exists. configFilePath = " + configFilePath);
        }

        if (!externalConfigFile.canRead()) {
            throw new IOException("logback config file cannot be read. configFilePath = " + configFilePath);
        }

        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);
        lc.reset();
        configurator.doConfigure(configFilePath);
        StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
    }
}
