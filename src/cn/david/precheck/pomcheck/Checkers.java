package cn.david.precheck.pomcheck;

import cn.david.precheck.util.Holder;

/**
 *
 * @author david
 * @since 2016年11月03日
 *
 * @ThreadSafe
 */
public class Checkers {

    private static final Holder<Checker> MULTISLF4JBINDERCHECKER_INSTANCE = new Holder<>();

    public static Checker newMultiSlf4jBinderChecker(boolean singleton) {
        if (singleton) {
            return getSingletonMultiSlf4jBinderChecker();
        } else {
            return new MultiSlf4jBinderChecker();
        }
    }

    private static Checker getSingletonMultiSlf4jBinderChecker() {
        Checker checker = MULTISLF4JBINDERCHECKER_INSTANCE.getValue();
        if (checker == null) {
            synchronized (MULTISLF4JBINDERCHECKER_INSTANCE) {
                if (checker == null) {
                    MULTISLF4JBINDERCHECKER_INSTANCE.setValue(new MultiSlf4jBinderChecker());
                    checker = MULTISLF4JBINDERCHECKER_INSTANCE.getValue();
                }
            }
        }
        return checker;
    }

}
