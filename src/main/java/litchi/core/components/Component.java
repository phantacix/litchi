//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.components;

/**
 * component interface
 *
 * @author 0x737263
 */
public interface Component {

    String name();

    void start();

    void afterStart();

    void beforeStop();

    void stop();

}
