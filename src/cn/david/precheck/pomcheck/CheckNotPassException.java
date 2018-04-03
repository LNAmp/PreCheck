package cn.david.precheck.pomcheck;

/**
 *
 * @author david
 * @since 2016年11月03日
 */
public class CheckNotPassException extends Exception {

    public CheckNotPassException() {
    }

    public CheckNotPassException(String message) {
        super(message);
    }

    public CheckNotPassException(String message, Throwable cause) {
        super(message, cause);
    }

    public CheckNotPassException(Throwable cause) {
        super(cause);
    }

    public CheckNotPassException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
