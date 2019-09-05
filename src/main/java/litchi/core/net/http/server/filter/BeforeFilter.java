//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.http.server.filter;

import litchi.core.net.http.server.HttpController;

import java.lang.reflect.Method;

public interface BeforeFilter {

    boolean execute(HttpController controller, Method method);

}
