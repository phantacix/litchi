//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.http.client;

public interface StringCallback {

	void completed(String content);

	void failed(Exception ex);

}
