//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.router;

import litchi.core.common.extend.ASMMethod;

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

    /**
     * 当前执行逻辑的方法
     */
    public ASMMethod method;

    /**
     * 线程id
     */
    public int threadId;

    /**
     * 服务器类型
     */
    public String nodeType;

    /**
     * 取请求参数的第x个索引做为hash计算
     */
    public int hashArgsIndex;

    /**
     * 序列化消息包的方法
     */
    public Method parseMethod;

    /**
     * 函数是否为void类型
     */
    public boolean isVoid;

    public RouteInfo() {
    }

    @Override
    public String toString() {
        return "RouteInfo{" +
                "routeName='" + routeName + '\'' +
                ", instance=" + instance +
                ", method=" + method +
                ", threadId=" + threadId +
                ", nodeType='" + nodeType + '\'' +
                ", hashArgsIndex=" + hashArgsIndex +
                ", parseMethod=" + parseMethod +
                ", isVoid=" + isVoid +
                '}';
    }

    public Object invoke(Object... args) {
        return this.method.invoke(args);
    }
}
