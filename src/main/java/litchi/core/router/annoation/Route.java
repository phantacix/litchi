//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.router.annoation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 路由注解，适合handler和rpc接口
 *
 * @author 0x737263
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Route {

    /**
     * 默认以类名为路由hanlder信息.
     * 设置则以该值为准
     * @return
     */
    String name() default "";

    /**
     * 默认的线程id
     * (初始化Dispatch对象时，需要添加当前服务器的线程信息)
     *
     * @return
     */
    int defaultThreadId();

    /**
     * 结点类型
     *
     * @return
     */
    String nodeType();

}
