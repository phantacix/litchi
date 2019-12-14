//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.common.extend;

import com.esotericsoftware.reflectasm.MethodAccess;

import java.lang.reflect.Method;

public class ASMMethod {
	private Method javaMethod;

	private MethodAccess access;
	private int index;
	private Object instance;

	private boolean isVoid;

	public static ASMMethod valueOf(Method method, Object instance) {
		ASMMethod asmMethod = new ASMMethod();
		asmMethod.javaMethod = method;
		asmMethod.access = MethodAccess.get(instance.getClass());
		asmMethod.index = asmMethod.access.getIndex(method.getName(), method.getParameterTypes());
		asmMethod.instance = instance;

		asmMethod.isVoid = void.class.equals(method.getReturnType());
		return asmMethod;
	}

	public Object invoke(Object... args) {
		return access.invoke(instance, index, args);
	}

	public Method getJavaMethod() {
		return this.javaMethod;
	}

	public boolean isVoid() {
		return this.isVoid;
	}
 }
