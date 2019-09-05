//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.http.server;

import java.util.Collection;

/**
 * @author 0x737263
 * Date:   $date$
 */
public interface ControllerFactory {

    Collection<Class<? extends HttpController>> getControllers();

    HttpController newInstance(Class<? extends HttpController> controller);
}
