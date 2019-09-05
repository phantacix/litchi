//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.jdbc.table;

/**
 * json序列化
 * 忽略字段@JSONField(serialize = false)
 * @author 0x737263
 *
 */
public abstract class JsonEntity {

	/**
	 * 从db读取数据之后，进行数据处理
	 */
	public void afterRead(Object obj) {
	}

	/**
	 * 实体写入db之前，进行数据处理
	 */
	public void beginWrite() {
	}

}
