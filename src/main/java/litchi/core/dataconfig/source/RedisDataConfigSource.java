//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.dataconfig.source;

import com.alibaba.fastjson.JSONObject;
import litchi.core.Constants;
import litchi.core.Litchi;
import litchi.core.common.utils.StringUtils;
import litchi.core.dataconfig.DataConfigSource;
import litchi.core.dataconfig.parse.DataParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * 配置文件redis源
 * @author Paopao
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
}
