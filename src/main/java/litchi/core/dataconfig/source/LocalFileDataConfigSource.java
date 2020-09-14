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
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
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

    private File fileDir;

    private int reloadFlushTime;

    private Litchi litchi;

    private Map<String, Long> fileCRCMaps = new HashMap<>();

    private FileAlterationMonitor reloadMonitor;

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

        if (fileConfig == null || fileConfig.isEmpty()) {
            LOGGER.error("'dataConfig' node is null. check litchi.json please.");
            return;
        }

        this.filePath = FileUtils.combine(litchi.getRootConfigPath(), fileConfig.getString("filePath"));

        this.reloadFlushTime = fileConfig.getInteger("reloadFlushTime");

        this.litchi = litchi;

        this.fileDir = new File(this.filePath);
        if (!this.fileDir.isDirectory()) {
            LOGGER.error("current data config path is not directory: path:{}", this.filePath);
            return;
        }

        try {
            reloadMonitor = new FileAlterationMonitor(this.reloadFlushTime, createFileObserver());
            reloadMonitor.start();

            LOGGER.info("local data config flush monitor is start!");
            LOGGER.info("path:{}, flushTime:{}ms", this.filePath, this.reloadFlushTime);
        } catch (Exception ex) {
            LOGGER.error("{}", ex);
        }
    }

    @Override
    public void destroy() {
        try {
            if(this.reloadMonitor != null) {
                reloadMonitor.stop();
            }
        } catch (Exception ex) {
            LOGGER.error("{}", ex);
        }
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

    private FileAlterationObserver createFileObserver() {
        IOFileFilter filterDirs = FileFilterUtils.and(FileFilterUtils.directoryFileFilter(),
                HiddenFileFilter.VISIBLE);
        IOFileFilter filterExtName = FileFilterUtils.and(FileFilterUtils.fileFileFilter(),
                FileFilterUtils.suffixFileFilter(this.getFileExtensionName()));

        IOFileFilter filter = FileFilterUtils.or(filterDirs, filterExtName);

        FileAlterationObserver observer = new FileAlterationObserver(this.fileDir, filter);
        observer.addListener(new FileAlterationListenerAdaptor() {
            public void onFileCreate(File file) {
                LOGGER.info("[create]" + file.getAbsolutePath());
                reloadProcess(file);
            }

            public void onFileChange(File file) {
                LOGGER.info("[change]:" + file.getAbsolutePath());
                reloadProcess(file);
            }

            public void onStart(FileAlterationObserver observer) {
                super.onStart(observer);
            }

            public void onStop(FileAlterationObserver observer) {
                super.onStop(observer);
            }
        });

        return observer;
    }

    private void reloadProcess(File f) {
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
                }

                LOGGER.warn("reload file:[{}] is [{}]", fileName, result ? "success" : "fail");
            } catch (Exception ex) {
                LOGGER.error("{}", ex);
            }
        }
    }

    @Override
    public Set<String> getConfigNames() {
        return fileCRCMaps.keySet();
    }
}
