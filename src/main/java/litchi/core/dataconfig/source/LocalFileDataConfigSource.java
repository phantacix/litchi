//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.dataconfig.source;

import com.alibaba.fastjson.JSONObject;
import litchi.core.Constants;
import litchi.core.Litchi;
import litchi.core.common.utils.CRCUtils;
import litchi.core.common.utils.FileUtils;
import litchi.core.common.utils.StringUtils;
import litchi.core.dataconfig.DataConfigSource;
import litchi.core.dataconfig.parse.DataParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 配置文件本地文件源
 *
 * @author Paopao
 */
public class LocalFileDataConfigSource implements DataConfigSource {
    private static Logger LOGGER = LoggerFactory.getLogger(LocalFileDataConfigSource.class);

    private DataParser dataParser;

    /**
     * 配置文件路径
     */
    private String filePath = "dataconfig" + File.separator;

    /**
     * 重新加载配置文件的路径
     */
    private String reloadPath = "newconfig" + File.separator;
    /**
     * 是否可以运行重新加载新的配置文件
     */
    private boolean reloadRunnable = true;

    private Litchi litchi;

    private Map<String, Long> fileCRCMaps = new HashMap<>();

    public LocalFileDataConfigSource() {
    }

    @Override
    public void initialize(Litchi litchi) {
        JSONObject config = litchi.config(Constants.Component.DATA_CONFIG);
        if (config == null) {
            LOGGER.error("'dataConfig' node not found in litchi.json");
            return;
        }

        String dataSourceKey = config.getString("dataSource");
        JSONObject fileConfig = config.getJSONObject(dataSourceKey);

        String filePath = FileUtils.combine(litchi.getRootConfigPath(), fileConfig.getString("filePath"));
        String reloadPath = FileUtils.combine(litchi.getRootConfigPath(), fileConfig.getString("reloadPath")) + File.separator;
        int reloadFlushTime = fileConfig.getInteger("reloadFlushTime");

        this.litchi = litchi;
        if (StringUtils.isNotBlank(filePath)) {
            this.filePath = filePath;
        }
        if (StringUtils.isNotBlank(reloadPath)) {
            this.reloadPath = reloadPath;
        }

        litchi.schedule().addEveryMillisecond(() -> {
            if (reloadRunnable) {
                checkFileUpdate();
            }
        }, reloadFlushTime);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void setDataParser(DataParser dataParser) {
        this.dataParser = dataParser;
    }

    @Override
    public String getConfigContent(String fileName) {
        String fullPath = getFullPath(fileName);
        String text = FileUtils.readFile(fullPath);
        if (StringUtils.isBlank(text)) {
            return text;
        }

        //add file crc value
        long crc = CRCUtils.calculateCRC(text);
        fileCRCMaps.put(fileName, crc);

        return text;
    }

    public String getFileExtensionName() {
        return dataParser.fileExtensionName();
    }

    /**
     * 根据文件名获取全路径
     *
     * @param fileName
     * @return
     */
    protected String getFullPath(String fileName) {
        return FileUtils.combine(this.filePath, fileName + getFileExtensionName());
    }

    /**
     * 检查文件更新
     */
    private void checkFileUpdate() {
        if (reloadRunnable == false) {
            return;
        }

        try {
            this.reloadRunnable = false;

            File dir = new File(this.reloadPath);
            if (!dir.isDirectory()) {
                LOGGER.error("current reload path is not directory: path:{}", this.reloadPath);
                return;
            }

            for (File f : dir.listFiles()) {
                if (f.isDirectory()) {
                    continue;
                }

                String fileName = f.getName().split("\\.")[0];

                String text = FileUtils.readFile(f);

                long newCRC = CRCUtils.calculateCRC(text);
                Long oldCRC = fileCRCMaps.get(f.getName());

                if (oldCRC == null || newCRC != oldCRC) {
                    try {
                        boolean result = litchi.data().checkModelAdapter(fileName, text);
                        if (result) {
                            //add new crc value
                            fileCRCMaps.put(fileName, newCRC);

                            litchi.data().reloadConfig(fileName, text);
                            fileMove2DataConfig(fileName, text);
                        }
                        LOGGER.warn("load file:[{}] is [{}]", fileName, result ? "success" : "fail");
                    } catch (Exception ex) {
                        LOGGER.error("{}", ex);
                    } finally {
                        f.delete();
                    }
                }
            }

        } catch (Exception ex) {
            LOGGER.error("{}", ex);
        } finally {
            this.reloadRunnable = true;
        }
    }

    private void fileMove2DataConfig(String fileName, String content) {
        String filePath = getFullPath(fileName);
        FileUtils.writeFile(new File(filePath), content);
    }

    @Override
    public Set<String> getConfigNames() {
        return fileCRCMaps.keySet();
    }
}
