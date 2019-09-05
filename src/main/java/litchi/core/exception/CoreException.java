//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.exception;

/**
 * core code exception
 * @author 0x737263
 */
public class CoreException extends RuntimeException {

    public CoreException(String text, Object... args) {
        super(String.format(text, args));
    }
}
