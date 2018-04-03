package cn.david.precheck.util;

/**
 *
 *
 * @author david
 * @since 2016年11月03日
 */
public class Holder<T> {

    private volatile T value;

    public Holder() {

    }

    public Holder(T value) {
        this.value = value;
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        this.value = value;
    }

}
