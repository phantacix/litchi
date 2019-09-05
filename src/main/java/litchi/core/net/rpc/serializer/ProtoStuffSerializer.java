//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.rpc.serializer;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProtoStuffSerializer implements Serializer {
	private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();

	@Override
	public <T> byte[] encode(T obj) throws IOException {

		Class<?> clazz = obj.getClass();
		LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
		try {
			@SuppressWarnings("unchecked")
			Schema<T> schema = (Schema<T>) getSchema(clazz);
			return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		} finally {
			buffer.clear();
		}
	}

	@Override
	public <T> T decode(byte[] bytes, Class<T> clazz) throws IOException {

		try {
			T t = clazz.newInstance();
			@SuppressWarnings("unchecked")
			Schema<T> schema = (Schema<T>) getSchema(clazz);
			ProtostuffIOUtil.mergeFrom(bytes, t, schema);
			return t;
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	private static Schema<?> getSchema(Class<?> clazz) {
		Schema<?> schema = cachedSchema.get(clazz);
		if (schema == null) {
			schema = RuntimeSchema.createFrom(clazz);
			if (schema != null) {
				cachedSchema.put(clazz, schema);
			}
		}
		return schema;
	}

}
