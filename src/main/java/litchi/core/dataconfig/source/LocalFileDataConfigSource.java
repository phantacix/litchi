//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.dataconfig.source;

import java.io.File;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import litchi.core.Constants;
import litchi.core.Litchi;
import litchi.core.common.utils.FileUtils;
import litchi.core.common.utils.HexUtils;
import litchi.core.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import litchi.core.dataconfig.DataConfigSource;
import litchi.core.dataconfig.parse.DataParser;

/**
 * 配置文件本地文件源
 *
 * @author Paopao
 * 2018-08-01
 */
public class LocalFileDataConfigSource implements DataConfigSource {

    private static Logger LOGGER = LoggerFactory.getLogger(LocalFileDataConfigSource.class);

//	/**
//	 * 定时线程(TODO 需要换成统一的调度池)
//	 */
//	private ScheduledExecutorService executorService;

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
    private boolean reloadRunable = true;

    private Litchi litchi;

    private Map<String, FileInfo> fileTimeMap = new HashMap<>();

    public LocalFileDataConfigSource() {
    }

    private void loadConfig(Litchi litchi, String parseClassName, String filePath, String reloadPath) {
        this.litchi = litchi;

        if (StringUtils.isNotBlank(filePath)) {
            this.filePath = filePath;
        }
        if (StringUtils.isNotBlank(reloadPath)) {
            this.reloadPath = reloadPath;
        }
    }

    @Override
    public void initialize(Litchi litchi) {
        JSONObject config = litchi.config(Constants.Component.DATA_CONFIG);
        if (config == null) {
            LOGGER.error("'dataconfig' node not found in litchi.json");
            return;
        }
        String dataSourceKey = config.getString("dataSource");
        JSONObject fileConfig = config.getJSONObject(dataSourceKey);

        String parseClassName = fileConfig.getString("parseClassName");
        String filePath = FileUtils.combine(litchi.getRootConfigPath(), fileConfig.getString("filePath"));
        String reloadPath = FileUtils.combine(litchi.getRootConfigPath(), fileConfig.getString("reloadPath")) + File.separator;
        int reloadFlushTime = fileConfig.getInteger("reloadFlushTime");
        loadConfig(litchi, parseClassName, filePath, reloadPath);

        litchi.schedule().addEveryMillisecond(() -> {
            if (reloadRunable) {
                checkFileUpdate();
            }
        }, reloadFlushTime);

//		executorService = Executors.newScheduledThreadPool(1);
//		// 定时扫描newconfig文件夹，确认是否要加载新的配置文件
//		executorService.scheduleWithFixedDelay(() -> {
//		}, reloadFlushTime, reloadFlushTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public void destroy() {
        //executorService.shutdownNow();
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

        if (fileTimeMap.containsKey(fileName) == false) {
            String md5 = md5(text);
            fileTimeMap.put(fileName, new FileInfo(System.currentTimeMillis(), md5));
        }
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
        if (reloadRunable == false) {
            return;
        }
        try {
            this.reloadRunable = false;

            for (Entry<String, FileInfo> entry : fileTimeMap.entrySet()) {
                String fileName = entry.getKey();
                String fullPath = this.reloadPath + fileName + getFileExtensionName();
                File file = new File(fullPath);
                if (!file.exists()) {
                    continue;
                }
                long lastModifiedTime = file.lastModified();
                if (entry.getValue().time == lastModifiedTime) {
                    continue;
                }
                entry.getValue().time = lastModifiedTime;

                String text = FileUtils.readFile(this.reloadPath, fileName + getFileExtensionName());
                boolean result = litchi.data().checkModelAdapter(fileName, text);
                if (result) {
                    String md5 = md5(text);
                    if (entry.getValue().md5.equals(md5)) {
                        file.delete();
                        continue;
                    }
                    litchi.data().reloadConfig(fileName, text);
                    entry.getValue().md5 = md5;
                    fileMove2DataConfig(fileName, text);
                }
                LOGGER.warn("load file:[{}] is [{}]", fileName, result ? "success" : "fail");
                file.delete();
            }
        } catch (Exception ex) {
            LOGGER.error("{}", ex);
        } finally {
            this.reloadRunable = true;
        }
    }

    /**
     * 将newConfig的文件加载到dataconfig文件中
     *
     * @param fileName
     * @param text
     */
    private void fileMove2DataConfig(String fileName, String text) {
        // 旧的配表路径
        String filePath = getFullPath(fileName);

        text = dataParser.format(text);
        // 覆盖旧文件
        FileUtils.writeFile(new File(filePath), text);
    }

    /**
     * md5加密
     *
     * @param src
     * @return
     */
    public static String md5(String src) {
        try {
            MessageDigest alg = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = alg.digest(src.getBytes("UTF-8"));
            return HexUtils.byte2Hex(md5Bytes);
        } catch (Exception ex) {
            LOGGER.error(src, ex);
        }
        return "";
    }

    private static class FileInfo {
        long time;
        String md5;

        public FileInfo(long time, String md5) {
            this.time = time;
            this.md5 = md5;
        }
    }

    @Override
    public Set<String> getConfigNames() {
        Set<String> set = new HashSet<>();
        fileTimeMap.keySet().stream().forEach(name -> set.add(name));
        return set;
    }

    @Override
    public String getConfig(String configName) {
        return null;
    }

    @Override
    public void removeConfig(String configName) {
    }

    @Override
    public void setConfig(String configName, String content) {
    }
}
