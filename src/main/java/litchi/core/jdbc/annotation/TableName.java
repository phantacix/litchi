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
 * db表名标注
 *
 * @author 0x737263
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TableName {

    /**
     * 数据库类型
     *
     * @return
     */
    String dbType();

    /**
     * 表名
     *
     * @return
     */
    String tableName();

    /**
     * 主键是否由idbuilder表来管理
     *
     * @return
     */
    boolean isIdBuilder() default false;

    /**
     * id自增初始值
     *
     * @return
     */
    long startId() default 0;
}
