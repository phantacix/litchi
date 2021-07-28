//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.router;

import litchi.core.common.extend.ASMMethod;
import litchi.core.common.utils.StringUtils;
import litchi.core.router.annotation.Handler;
import litchi.core.router.annotation.Route;
import litchi.core.router.annotation.Rpc;

import java.lang.reflect.Method;

/**
 * 路由信息
 */
public class RouteInfo {

    /**
     * nodeType.className.MethodName
     */
    public String routeName;

    /**
     * route对象实例
     */
    public BaseRoute instance;

    public Class<?> instanceClazz;

    /**
     * 当前执行逻辑的方法
     */
    public ASMMethod method;

    /**
     * 线程id
     */
    public int threadId;

    public Route routeAnnotation;
    public Rpc rpcAnnotation;
    public Handler handlerAnnotation;


    public RouteInfo() {
    }

    public static RouteInfo valueOf(BaseRoute instance, Class<?> clazz, Route routeAnnotation, Method method) {
        Rpc rpcAnnotation = method.getAnnotation(Rpc.class);
        Handler handlerAnnotation = method.getAnnotation(Handler.class);

        if (rpcAnnotation == null && handlerAnnotation == null) {
            return null;
        }

        RouteInfo routeInfo = new RouteInfo();
        routeInfo.instance = instance;
        routeInfo.instanceClazz = clazz;
        routeInfo.method = ASMMethod.valueOf(method, instance);
        routeInfo.routeAnnotation = routeAnnotation;

        if(rpcAnnotation != null) {
            routeInfo.rpcAnnotation = rpcAnnotation;
            routeInfo.threadId = rpcAnnotation.threadId() > 0 ? rpcAnnotation.threadId() : routeAnnotation.defaultThreadId();
            routeInfo.routeName = routeInfo.buildRouteName(clazz, routeAnnotation, method, "");
        }

        if(handlerAnnotation != null) {
            routeInfo.handlerAnnotation = handlerAnnotation;
            routeInfo.threadId = handlerAnnotation.threadId() > 0 ? handlerAnnotation.threadId() : routeAnnotation.defaultThreadId();
            routeInfo.routeName = routeInfo.buildRouteName(clazz, routeAnnotation, method, handlerAnnotation.name());
        }

        return routeInfo;
    }

    /**
     * 结点类型
     * @return
     */
    public String nodeType() {
        return this.routeAnnotation.nodeType();
    }

    public String buildRouteName(Class<?> clazz, Route annotationRoute, Method method, String methodName) {
        return String.join(".",
                annotationRoute.nodeType(),
                StringUtils.isBlank(annotationRoute.name()) ? clazz.getSimpleName() : annotationRoute.name(),
                StringUtils.isBlank(methodName) ? method.getName() : methodName);
    }

    public Object invoke(Object... args) {
        return this.method.invoke(args);
    }
}
