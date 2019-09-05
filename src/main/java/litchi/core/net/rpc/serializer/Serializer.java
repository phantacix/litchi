//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.rpc.serializer;

import java.io.IOException;

public interface Serializer {

	<T> byte[] encode(T obj) throws IOException;

	<T> T decode(byte[] bytes, Class<T> clazz) throws IOException;
}
