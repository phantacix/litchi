//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.event.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * receive event annotation
 *
 * @author 0x737263
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
public @interface EventReceive {

    /**
     * 事件名
     *
     * @return
     */
    String[] name();

    /**
     * 执行的线程池id
     *
     * @return
     */
    int threadId() default -1;

}
