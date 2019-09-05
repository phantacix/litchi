package litchi.core.dataconfig.annotation;
//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 映射字段
 * @author 0x737263
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface FieldName {

	/**
	 * 自定义字段名，默认取Field.getName()
	 * @return
	 */
	String newName() default "";

	/**
	 * 索引名,默认为无索引。可自行添加
	 * @return
	 */
	String indexName() default "";

}