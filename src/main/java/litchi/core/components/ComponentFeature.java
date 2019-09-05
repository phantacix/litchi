//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.components;

import litchi.core.Litchi;

public interface ComponentFeature<T> {

    T createComponent(Litchi ph);
}
