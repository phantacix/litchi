//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.dataconfig;

import litchi.core.Litchi;
import litchi.core.dataconfig.parse.DataParser;

import java.util.Set;

public interface DataConfigSource {

	void initialize(Litchi litchi);

	void destroy();

	void setDataParser(DataParser dataParser);

	String getConfigContent(String fileName);

	Set<String> getConfigNames();
}
