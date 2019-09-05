//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.rpc;

import litchi.core.net.rpc.serializer.ProtoStuffSerializer;
import litchi.core.net.rpc.serializer.Serializer;

public class RpcConfig {

	/** 请求超时 */
	public static final int RPC_TIMEOUT = 10000;

	// public static Serializer SERIALIZER = KryoSerializer.getInstance();
	// public static Serializer SERIALIZER = new Hessian2Serializer();
	private static Serializer SERIALIZER = new ProtoStuffSerializer();

	public static Serializer getSerializer() {
		return SERIALIZER;
	}
}
