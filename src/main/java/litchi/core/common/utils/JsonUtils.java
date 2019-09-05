//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class JsonUtils {

    public static JSONObject read(String first, String... paths) {
        String json = FileUtils.readFile(first, paths);
        return JSON.parseObject(json);
    }

    public static String[] readStringArray(JSONObject jsonObject, String item) {
        JSONArray basePackagesArray = jsonObject.getJSONArray(item);
        String[] value = new String[basePackagesArray.size()];
        basePackagesArray.toArray(value);

        return value;
    }

    public static String getString(JSONObject jsonObject, String key, String defaultValue) {
        String value = jsonObject.getString(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public static int getInt(JSONObject jsonObject, String key, int defaultValue) {
        Integer value = jsonObject.getInteger(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
    
    public static boolean getBoolean(JSONObject jsonObject, String key, boolean defaultValue) {
    	Boolean value = jsonObject.getBoolean(key);
    	if (value == null) {
    		return defaultValue;
    	}
    	return value;
    }
    
}
