//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.http.server.router;

import java.lang.reflect.Method;

import litchi.core.net.http.server.HttpController;
import litchi.core.net.http.server.filter.BeforeFilter;

public class RouteAction {

    public Class<? extends HttpController> controllerClazz;

    public Method method;

    public BeforeFilter[] actionFilters;

    public BeforeFilter[] controllerFilters;
    
    public static RouteAction valueOf(Class<? extends HttpController> clazz, Method method, BeforeFilter[] actionFilters, BeforeFilter[] controllerFilters) {
        RouteAction action = new RouteAction();
        action.controllerClazz = clazz;
        action.method = method;
        action.actionFilters = actionFilters;
        action.controllerFilters = controllerFilters;
        return action;
    }
}
