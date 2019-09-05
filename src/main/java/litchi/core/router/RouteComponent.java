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
import litchi.core.router.annoation.Handler;
import litchi.core.router.annoation.Route;
import litchi.core.router.annoation.Rpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import litchi.core.common.extend.ASMMethod;
import litchi.core.common.utils.StringUtils;

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

    public RouteInfo getRouteInfo(String route) {
        return routeMaps.get(route);
    }

    public int getThreadId(String route) {
        RouteInfo routeInfo = getRouteInfo(route);
        return routeInfo.threadId;
    }

    private void addRouteInfo(RouteInfo routeInfo) {
        if (routeMaps.containsKey(routeInfo.routeName)) {
            LOGGER.warn("----------------- route= {} contains in route maps", routeInfo.routeName);
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

        Route annotationRoute = clazz.getAnnotation(Route.class);
        Method[] methodArray = clazz.getMethods();
        for (Method method : methodArray) {
            method.setAccessible(true);
            loadRpcRouteInfo(instance, clazz, annotationRoute, method);
            loadHandlerRouteInfo(instance, clazz, annotationRoute, method);
        }

        litchi.event().register(instance);
    }

    private void loadRpcRouteInfo(BaseRoute instance, Class<?> clazz, Route annotationRoute, Method method) {
        Rpc requestRpc = method.getAnnotation(Rpc.class);
        if (requestRpc == null) {
            return;
        }

        RouteInfo routeInfo = new RouteInfo();
        routeInfo.instance = instance;
        routeInfo.method = ASMMethod.valueOf(method, instance);
        routeInfo.threadId = requestRpc.threadId() > 0 ? requestRpc.threadId() : annotationRoute.defaultThreadId();
        routeInfo.hashArgsIndex = requestRpc.hashArgsIndex();
        routeInfo.nodeType = annotationRoute.nodeType();
        routeInfo.routeName = buildRouteName(clazz, annotationRoute, method, "");
        routeInfo.isVoid = isVoidType(method);

        this.addRouteInfo(routeInfo);
    }

    private void loadHandlerRouteInfo(BaseRoute instance, Class<?> clazz, Route annotationRoute, Method method) {
        Handler requestHandler = method.getAnnotation(Handler.class);
        if (requestHandler == null) {
            return;
        }

        RouteInfo routeInfo = new RouteInfo();
        routeInfo.instance = instance;
        routeInfo.method = ASMMethod.valueOf(method, instance);
        routeInfo.threadId = requestHandler.threadId() > 0 ? requestHandler.threadId() : annotationRoute.defaultThreadId();
        routeInfo.hashArgsIndex = requestHandler.hashArgsIndex();
        routeInfo.nodeType = annotationRoute.nodeType();
        routeInfo.routeName = buildRouteName(clazz, annotationRoute, method, requestHandler.name());
        routeInfo.isVoid = isVoidType(method);

        try {
            //找到pb request对象相关的反射对象
            Class<?>[] parameterClazz = method.getParameterTypes();
            Class lastClazz = parameterClazz[parameterClazz.length - 1];
            routeInfo.parseMethod = lastClazz.getMethod("parseFrom", byte[].class);
        } catch (Exception ex) {
            LOGGER.error("{}", ex);
        }

        this.addRouteInfo(routeInfo);
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

    private String buildRouteName(Class<?> clazz, Route annotationRoute, Method method, String methodName) {
        return String.join(".",
                annotationRoute.nodeType(),
                StringUtils.isBlank(annotationRoute.name()) ? clazz.getSimpleName() : annotationRoute.name(),
                StringUtils.isBlank(methodName) ? method.getName() : methodName);
    }

    private boolean isVoidType(Method method) {
        return void.class.equals(method.getReturnType());
    }
}
