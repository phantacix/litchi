//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.jdbc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * db字段标注,被加的字段必需为public,否则序列化有问题
 * @author 0x737263
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface Column {

	/**
	 * 是否为主键。默认为false
	 * @return
	 */
	boolean pk() default false;

	/**
	 * 字段别名(注意字段顺序),  暂不支持映射多个字段-_-|  .eg..{"column1","column2"}
	 * @return
	 */
	String alias() default "";

}
