//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.dataconfig;

import java.util.Set;

import litchi.core.Litchi;
import litchi.core.dataconfig.parse.DataParser;

public interface DataConfigSource {

	void initialize(Litchi litchi);
	
	void destroy();
	
	void setDataParser(DataParser dataParser);
	
	String getConfigContent(String fileName);

	Set<String> getConfigNames();

	String getConfig(String configName);

	void removeConfig(String configName);

	void setConfig(String configName, String content);

}
