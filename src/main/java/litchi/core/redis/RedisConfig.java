//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.redis;

import java.util.UUID;

import com.alibaba.fastjson.JSONObject;

import litchi.core.common.utils.JsonUtils;
import litchi.core.common.utils.StringUtils;
import litchi.core.common.utils.JsonUtils;
import litchi.core.common.utils.StringUtils;

/**
 * @author Phil
 * Date:   2018/3/24
 */
public class RedisConfig {

    /**
     * redis标识
     */
    private String key;
    
    /**
     * 是否属于均衡redis实例
     */
    private boolean shared;
    /**
     * ip
     */
    private String host;
    /**
     * 端口
     */
    private int port;
    /**
     * 密码
     */
    private String password;
    /**
     * 使用的数据库序号
     */
    private int dbIndex;
    /**
     * 最大连接数
     */
    private int maxConnect;
    /**
     * 最大空闲连接数
     */
    private int maxIdleConnect;
    /**
     * 最小空闲连接数
     */
    private int minIdleConnect;

    public RedisConfig(JSONObject jsonObject, int dbIndex) {
        this.key = JsonUtils.getString(jsonObject, "id", UUID.randomUUID().toString());
        this.host = JsonUtils.getString(jsonObject, "host", "");
        this.port = JsonUtils.getInt(jsonObject, "port", 0);
        this.password = JsonUtils.getString(jsonObject, "password", "");
        this.shared = JsonUtils.getBoolean(jsonObject, "shared", false);
        this.maxConnect = JsonUtils.getInt(jsonObject, "maxConnect", 0);
        this.maxIdleConnect = JsonUtils.getInt(jsonObject, "maxIdleConnect", 0);
        this.minIdleConnect = JsonUtils.getInt(jsonObject, "minIdleConnect", 0);
        this.dbIndex = dbIndex;
    }

    public RedisConfig(String key, String host, int port, String password, int dbIndex) {
        this.key = key;
        this.host = host;
        this.port = port;
        this.password = password;
        this.dbIndex = dbIndex;
    }

    public RedisConfig(String key, String host, int port) {
        this(key, host, port, "", 0);
    }

    public String getKey() {
        return key;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getPassword() {
        return password;
    }
    
    public int getDbIndex() {
		return dbIndex;
	}

    public boolean hasPassword() {
        if (StringUtils.isNotBlank(password)) {
            return true;
        }
        return false;
    }

	public boolean isShared() {
		return shared;
	}
	
	public int getMaxConnect() {
		return maxConnect;
	}
	
	public int getMaxIdleConnect() {
		return maxIdleConnect;
	}
	
	public int getMinIdleConnect() {
		return minIdleConnect;
	}
	
}
