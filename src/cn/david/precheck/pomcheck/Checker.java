package cn.david.precheck.pomcheck;

/**
 *
 *
 * @author david
 * @since 2016年11月03日
 */
public interface Checker<T> {

    void check(T t) throws CheckNotPassException;

}
