//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.common.extend;

/**
 * 
 * @author 0x737263
 *
 * @param <T>
 */
public class ObjectReference <T> {

	/**
	 * 软引用对象 
	 */
	private T content;

	public T get() {
		return content;
	}

	public void set(T content) {
		this.content = content;
	}

	
}
