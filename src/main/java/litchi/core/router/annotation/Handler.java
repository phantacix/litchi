//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.router.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 被请求的handler方法需要标注
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Handler {

    /**
     * 路由的方法名
     * 默认使用Method的方法名,设置该值则以name()为准
     *
     * @return
     */
    String name() default "";

    /**
     * 默认读取@Route注解的defaultThreadId()
     * 否则以该值为准
     *
     * @return
     */
    int threadId() default -1;

    /**
     * 是否需要登陆验证
     *
     * @return
     */
    boolean isLogin() default true;
}
