//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.dataconfig.source;

import java.util.Set;

import litchi.core.Constants;
import litchi.core.Litchi;
import litchi.core.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import litchi.core.dataconfig.DataConfigSource;
import litchi.core.dataconfig.parse.DataParser;

/**
 * 配置文件redis源
 * @author Paopao
 * 		   2018-08-01
 */
public class RedisDataConfigSource implements DataConfigSource {
	
	private static Logger LOGGER = LoggerFactory.getLogger(RedisDataConfigSource.class);
	
	private Litchi litchi;
	
	private String redisKey;
	
	private boolean blockOnConfigEmpty = true;
	
	public RedisDataConfigSource() {
	}
	
	@Override
	public void initialize(Litchi litchi) {
		this.litchi = litchi;
		this.blockOnConfigEmpty = litchi.currentNode().isLackConfigFileIsExit();
		JSONObject config = litchi.config(Constants.Component.DATA_CONFIG);
		String dataSourceKey = config.getString("dataSource");
		JSONObject redisConfig = config.getJSONObject(dataSourceKey);
		redisKey = redisConfig.getString("redisKey");
		if (StringUtils.isBlank(redisKey)) {
			LOGGER.error("data config redis key is empty. please config it in {}", Constants.Component.DATA_CONFIG);
			return;
		}
	}


	@Override
	public String getConfigContent(String fileName) {
		if (StringUtils.isBlank(redisKey)) {
			return "";
		}
		String configText = litchi.redis().hash().get(redisKey, fileName);
		if (StringUtils.isBlank(configText)) {
			LOGGER.error("data config not exist in redis, key={} fileName={}", redisKey, fileName);
			if (blockOnConfigEmpty) {
				System.exit(1);
			}
		}
		if (configText == null) {
			return "";
		}
		return configText;
	}

	@Override
	public void destroy() {
		
	}

	@Override
	public void setDataParser(DataParser dataParser) {
		
	}
	
	public String getRedisKey() {
		return redisKey;
	}

	@Override
	public Set<String> getConfigNames() {
		Set<String> configs = litchi.redis().hash().keys(getRedisKey());
		return configs;
	}

	@Override
	public String getConfig(String configName) {
		String config = litchi.redis().hash().get(getRedisKey(), configName);
		return config;
	}

	@Override
	public void removeConfig(String configName) {
		litchi.redis().hash().del(getRedisKey(), configName);
	}

	@Override
	public void setConfig(String configName, String content) {
		litchi.redis().hash().set(getRedisKey(), configName, content);
	}

}
