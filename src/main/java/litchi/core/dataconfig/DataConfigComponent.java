//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.dataconfig;

import com.alibaba.fastjson.JSONObject;
import litchi.core.Constants;
import litchi.core.Litchi;
import litchi.core.common.utils.PathResolver;
import litchi.core.common.utils.StringUtils;
import litchi.core.dataconfig.annotation.DataFile;
import litchi.core.dataconfig.annotation.FieldName;
import litchi.core.dataconfig.annotation.IndexPK;
import litchi.core.dataconfig.parse.DataParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 配置文件存储和管理
 *
 * @author Paopao
 * 2018-07-31
 */
public class DataConfigComponent implements DataConfig {
    protected Logger LOGGER = LoggerFactory.getLogger(getClass());

    /**
     * 数据存储结构
     */
    protected DataStorage dataStorage;

    /**
     * 配置数据源
     */
    private DataConfigSource dataSource;

    /**
     * 数据格式解析器
     */
    private DataParser dataParser;

    /**
     * 文件映射class所在包路径列表
     */
    private String[] packagesName;

    private Set<ConfigService> configServicesList = new HashSet<>();

    public DataConfigComponent(Litchi litchi) {
        JSONObject config = litchi.config(Constants.Component.DATA_CONFIG);
        String dataSourceKey = config.getString("dataSource");
        JSONObject dataSourceConfig = config.getJSONObject(dataSourceKey);

        String parseClassName = dataSourceConfig.getString("parseClassName");
        if (StringUtils.isBlank(parseClassName)) {
            parseClassName = "litchi.core.dataconfig.parse.JSONDataParser";
        }

        String sourceClassName = dataSourceConfig.getString("sourceClassName");
        if (StringUtils.isBlank(sourceClassName)) {
            //default read local config file.
            sourceClassName = "litchi.core.dataconfig.source.LocalFileDataConfigSource";
        }

        String[] basePackages = litchi.packagesName();
        DataParser dataParser = PathResolver.scanAndNewInstance(basePackages, parseClassName, DataParser.class);

        DataConfigSource dataSource = PathResolver.scanAndNewInstance(basePackages, sourceClassName, DataConfigSource.class);
        dataSource.setDataParser(dataParser);
        dataSource.initialize(litchi);

        this.dataStorage = new DataStorage();
        this.packagesName = basePackages;
        this.dataSource = dataSource;
        this.dataParser = dataParser;
    }

    @Override
    public DataConfigSource getDataSource() {
        return dataSource;
    }

    @Override
    public <T extends ConfigAdapter> T getFirst(Class<T> modelClass, IndexObject indexObject, Object... params) {
        return this.dataStorage.getFirst(modelClass, indexObject, params);
    }

    @Override
    public <T extends ConfigAdapter> List<T> getList(Class<T> modelClazz) {
        return this.dataStorage.getList(modelClazz);
    }

    @Override
    public <T extends ConfigAdapter> List<T> getList(Class<T> modelClass, IndexObject indexObject, Object... params) {
        return this.dataStorage.getList(modelClass, indexObject, params);
    }

    @Override
    public <T extends ConfigAdapter> T getModel(Class<T> modelClass, Object id) {
        return this.dataStorage.getModel(modelClass, id);
    }

    @Override
    public Class<ConfigAdapter> getClassByFileName(String fileName) {
        return this.dataStorage.getClassByFileName(fileName);
    }

    /**
     * 初始化ConfigAdapter
     */
    private void initConfigAdapterList() {
        Map<String, Boolean> loadFileMaps = new HashMap<>();

        // 通过包名扫描获取对应的类集合
        Collection<Class<? extends ConfigAdapter>> collection = PathResolver.scanPkg(DataFile.class, ConfigAdapter.class, packagesName);
        if (collection == null || collection.isEmpty()) {
            LOGGER.error("in package [{}] class not found.", Arrays.asList(packagesName));
            return;
        }

        collection.forEach(clazz -> {
            String fileName = getConfigFileName(clazz);
            String configContent = dataSource.getConfigContent(fileName);
            if (StringUtils.isBlank(configContent)) {
                LOGGER.error("config content not found. fileName={} class={}", fileName, clazz);
                return;
            }

            @SuppressWarnings("unchecked")
            boolean result = initConfigAdapter((Class<ConfigAdapter>) clazz, configContent);
            loadFileMaps.put(clazz.getSimpleName(), result);
        });

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("load data config file list:");
            loadFileMaps.forEach((key, value) -> LOGGER.debug("result:{}, load file:{}", value, key));
        }
    }

    /**
     * 初始化
     *
     * @param clazz
     */
    public boolean initConfigAdapter(Class<ConfigAdapter> clazz, String text) {
        try {
            if (clazz == null) {
                return false;
            }

            List<ConfigAdapter> list = this.dataParser.parse(text, clazz);
            if (list == null) {
                return false;
            }

            for (ConfigAdapter obj : list) {
                try {
                    obj.initialize();
                } catch (Exception e) {
                    List<Field> indexFields = new ArrayList<>();
                    Field[] declaredFields = obj.getClass().getDeclaredFields();
                    for (Field field : declaredFields) {
                        if (field.isAnnotationPresent(IndexPK.class)) {
                            indexFields.add(field);
                            break;
                        } else {
                            FieldName fieldName = field.getAnnotation(FieldName.class);
                            if (fieldName != null && StringUtils.isNotBlank(fieldName.indexName())) {
                                indexFields.add(field);
                            }
                        }
                    }
                    Map<String, Object> indexMap = new HashMap<>();
                    for (Field field : indexFields) {
                        field.setAccessible(true);
                        indexMap.put(field.getName(), field.get(obj));
                    }
                    LOGGER.error("load data data [{}] error. index:{}", clazz.getName(), indexMap);
                    LOGGER.error("", e);
                }
            }

            String fileName = getConfigFileName(clazz);
            Storage storage = new Storage(fileName, clazz, list);
            this.dataStorage.addStorage(storage);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("storage info:{}", storage.toString());
            }
            return true;
        } catch (Exception e) {
            LOGGER.error(String.format("file: [%s] read error!", clazz.getName()), e);
            return false;
        }
    }

    /**
     * 重新加载配置数据
     *
     * @param fileName
     * @param text
     */
    public void reloadConfig(String fileName, String text) {
        Class<ConfigAdapter> clazz = getClassByFileName(fileName);
        // 重载对应类的model
        if (initConfigAdapter(clazz, text)) {
            //调用clean方法
            configServicesList.forEach(item -> item.clean(clazz));
            configServicesList.forEach(item -> item.initialize(this));

            LOGGER.info("reload file:[{}] success.", fileName);
            return;
        }
    }

    public boolean checkModelAdapter(String fileName, String text) {
        Class<ConfigAdapter> clazz = getClassByFileName(fileName);
        if (clazz == null) {
            return false;
        }

        try {
            List<ConfigAdapter> list = this.dataParser.parse(text, clazz);
            if (list.size() < 1) {
                return false;
            }
            return true;
        } catch (Exception e) {
            LOGGER.error(String.format("file: [%s] read error!", clazz.getName()), e);
        }

        return false;
    }

    /**
     * 获取配置文件名
     *
     * @param clazz
     * @return
     */
    protected String getConfigFileName(Class<? extends ConfigAdapter> clazz) {
        DataFile df = clazz.getAnnotation(DataFile.class);
        String fileName;
        String definedName = df.fileName();
        if (StringUtils.isNotBlank(definedName)) {
            fileName = definedName;
        } else {
            fileName = StringUtils.firstCharLowerCase(clazz.getSimpleName());
        }
        return fileName;
    }

    /**
     * 注册配置管理器
     *
     * @param configService
     */
    public void registerService(ConfigService configService) {
        configServicesList.add(configService);
        configService.initialize(this);
    }

    @Override
    public String name() {
        return Constants.Component.DATA_CONFIG;
    }

    @Override
    public void start() {
        initConfigAdapterList();
    }

    @Override
    public void afterStart() {
        LOGGER.info("Default Data Config is started!");
    }

    @Override
    public void stop() {
        this.dataSource.destroy();
        LOGGER.info("Default Data Config is destroy!");
    }

    @Override
    public void beforeStop() {

    }
}
