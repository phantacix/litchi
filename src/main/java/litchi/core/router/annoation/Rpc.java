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
 * 描述服务接口信息，rpc服务器部份使用
 *
 * @author 0x737263
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Rpc {

    /**
     * 指定该方法的派发类型
     *
     * @return
     */
    int threadId() default -1;

    /**
     * 派发时以调用方法的第x个参数的索引做为hash值
     * 默认为方法参数的第0个
     *
     * @return
     */
    int hashArgsIndex() default 0;

}
