//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.http.server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * url路由注解
 * @author 0x737263
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface UrlRouter {

	/**
	 * "/user"
	 * "/user/:id"
	 * @return
	 */
	String[] path() default "";

	/**
	 * 允许post
	 * @return
	 */
	boolean post() default true;

	/**
	 * 允许get
	 * @return
	 */
	boolean get() default true;
}
