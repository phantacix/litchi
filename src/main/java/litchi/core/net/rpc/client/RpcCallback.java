//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.rpc.client;

public interface RpcCallback<R> {

	/**
	 * 回调结果
	 * @param result
	 */
	void result(R result);

}
