package litchi.core.test.protostuff;

import com.alibaba.fastjson.annotation.JSONField;

public class AClazz {

    @JSONField(ordinal = 1)
    public int zint;
    @JSONField(ordinal = 2)
    public String bString;
    @JSONField(ordinal = 3)
    public String aString;
    @JSONField(ordinal = 4)
    public int aInt;
}
