//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.redis;

/**
 * @author Phil
 * Date:   2018/3/24
 */
public class RedisConfigErrorException extends RuntimeException {

	private static final long serialVersionUID = -1394363760139586051L;

	public RedisConfigErrorException() {
    }

    public RedisConfigErrorException(String message) {
        super(message);
    }
}
