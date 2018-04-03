package cn.david.precheck.pomcheck;

import java.util.Map;
import java.util.Set;

/**
 *
 * @author david
 * @since 2016年11月03日
 *
 */
public class MultiSlf4jBinderChecker implements LevelChecker<Object>, NodeChecker<Object> {


    @Override
    @SuppressWarnings("unchecked")
    public void check(Object target) throws CheckNotPassException {

        if (target instanceof Map) {
            Map<String, Object> targetMap = (Map<String, Object>) target;
            Set<String> logDependencySet = (Set<String>) targetMap.get(MavenDependencyUtil.SLF4J_KEY);
            if (logDependencySet == null) {
                return;
            }
            if (logDependencySet.size() > 1) {
                throw new MultiSlf4jBinderException("More than one binder!", logDependencySet);
            }
        }


    }
}
