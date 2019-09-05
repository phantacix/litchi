//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.common.extend;

/**
 * @author 0x737263
 */
public class Dual<O, T> {
    private O one;

    private T two;


    public Dual() {
    }

    public Dual(O one, T two) {
        this.one = one;
        this.two = two;
    }

    public O getOne() {
        return one;
    }

    public T getTwo() {
        return two;
    }

    public void setOne(O one) {
        this.one = one;
    }

    public void setTwo(T two) {
        this.two = two;
    }

    @Override
    public boolean equals(Object obj) {
        @SuppressWarnings("unchecked")
        Dual<O, T> dual = (Dual<O, T>) obj;
        if (this.one != null && this.one.equals(dual.one) && this.two != null && this.two.equals(dual.two)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "[" + one + ", " + two + "]";
    }

}
