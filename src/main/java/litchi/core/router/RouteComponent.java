//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.router;

import litchi.core.Constants;
import litchi.core.Litchi;
import litchi.core.components.Component;
import litchi.core.exception.CoreException;
import litchi.core.router.annotation.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * route component
 *
 * @author 0x737263
 */
public class RouteComponent implements Component {
    static Logger LOGGER = LoggerFactory.getLogger(RouteComponent.class);

    Litchi litchi;

    /**
     * handler&rpc
     * key: route, value: RouteInfo
     */
    private Map<String, RouteInfo> routeMaps = new HashMap<>();

    public RouteComponent(Litchi litchi) {
        this.litchi = litchi;
    }

    @Override
    public String name() {
        return Constants.Component.ROUTE;
    }

    @Override
    public void start() {
    }

    @Override
    public void afterStart() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void beforeStop() {

    }

    public RouteInfo getRouteInfo(String route) {
        return routeMaps.get(route);
    }

    public int getThreadId(String route) {
        RouteInfo routeInfo = getRouteInfo(route);
        return routeInfo.threadId;
    }

    private void addRouteInfo(RouteInfo routeInfo) {
        if (routeMaps.containsKey(routeInfo.routeName)) {
            LOGGER.warn("----------------> route= {} contains in route maps", routeInfo.routeName);
            throw new CoreException("routeName duplicate");
        }
        routeMaps.put(routeInfo.routeName, routeInfo);
    }

    public void putAll(Collection<BaseRoute> baseRoutes) {
        for (BaseRoute route : baseRoutes) {
            put(route);
        }
    }

    public void putAll(BaseRoute... baseRoutes) {
        for (BaseRoute route : baseRoutes) {
            put(route);
        }
    }

    public void put(BaseRoute instance) {
        if (instance == null) {
            return;
        }

        Class<?> clazz = getAnnotationClazz(instance);
        if (clazz == null) {
            LOGGER.warn("BaseRoute not use @Route annotation. class = {}", instance.getClass().getName());
            return;
        }

        Route routeAnnotation = clazz.getAnnotation(Route.class);
        Method[] methodArray = clazz.getMethods();
        for (Method method : methodArray) {
            method.setAccessible(true);

            RouteInfo routeInfo = RouteInfo.valueOf(instance, clazz, routeAnnotation, method);
            if(routeInfo != null) {
                this.addRouteInfo(routeInfo);
            }
        }

        litchi.event().register(instance);
    }

    private Class<?> getAnnotationClazz(BaseRoute instance) {
        Class<?> annotationClazz = null;
        Route annotationRoute = instance.getClass().getAnnotation(Route.class);
        if (annotationRoute != null) {
            annotationClazz = instance.getClass();
        } else {
            Class<?>[] interfacesClazz = instance.getClass().getInterfaces();
            for (Class<?> clazz : interfacesClazz) {
                annotationRoute = clazz.getAnnotation(Route.class);
                if (annotationRoute != null) {
                    annotationClazz = clazz;
                    break;
                }
            }
        }
        return annotationClazz;
    }
}
