package cn.david.precheck.pomcheck;

import java.util.Set;

/**
 *
 *
 * @author david
 * @since 2016年11月03日
 */
public class MultiSlf4jBinderException extends CheckNotPassException {

    final Set<String> slf4jBinders;

    public MultiSlf4jBinderException(String message, Set<String> slf4jBinders) {
        super(message);
        this.slf4jBinders = slf4jBinders;
    }

    public Set<String> getSlf4jBinders() {
        return slf4jBinders;
    }

}
