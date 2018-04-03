package cn.david.precheck.pomcheck;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 *
 * @author david
 * @since 2016年11月03日
 *
 */
public class CheckerManager {

    private Collection<Checker> allCheckers = new ArrayList<>();

    private Collection<NodeChecker> nodeCheckers = new ArrayList<>();

    private Collection<LevelChecker> levelCheckers = new ArrayList<>();

    private final ReentrantLock lock = new ReentrantLock();

    public CheckerManager() {

    }

    public CheckerManager(Collection<Checker> checkers) {
        addCheckers(checkers);
    }

    public Collection<Checker> getAllCheckers() {
        return Collections.unmodifiableCollection(allCheckers);
    }

    public Collection<Checker> getLevelCheckers() {
        return Collections.unmodifiableCollection(levelCheckers);
    }

    public Collection<Checker> getNodeCheckers() {
        return Collections.unmodifiableCollection(nodeCheckers);
    }

    public void addChecker(Checker checker) {
        try {
            lock.lock();
            addCheckerInternal(checker);
        } catch (Throwable t){

        } finally {
            lock.unlock();
        }
    }

    public void addCheckers(Collection<Checker> checkers) {
        try {
            lock.lock();
            addCheckersInternal(checkers);
        } catch (Throwable t){

        } finally {
            lock.unlock();
        }
    }

    public void removeChecker(Checker checker) {
        try {
            lock.lock();
            removeCheckerInternal(checker);
        } catch (Throwable t){

        } finally {
            lock.unlock();
        }
    }

    public void fireAllCheck(Object target) throws CheckNotPassException{
        for (Checker checker : allCheckers) {
            checker.check(target);
        }
    }

    public void fireNodeCheck(Object target) throws CheckNotPassException {
        for (NodeChecker checker : nodeCheckers) {
            checker.check(target);
        }
    }

    public void fireLevelCheck(Object target) throws CheckNotPassException {
        for (LevelChecker checker : levelCheckers) {
            checker.check(target);
        }
    }

    private void addCheckerInternal(Checker checker) {
        allCheckers.add(checker);

        if (checker instanceof NodeChecker) {
            nodeCheckers.add((NodeChecker) checker);
        }

        if (checker instanceof LevelChecker) {
            levelCheckers.add((LevelChecker) checker);
        }
    }

    private void addCheckersInternal(Collection<Checker> checkers) {
        if (checkers != null && !checkers.isEmpty()) {
            for (Checker checker : checkers) {
                addCheckerInternal(checker);
            }
        }
    }

    private void removeCheckerInternal(Checker checker) {
        allCheckers.remove(checker);
        if (checker instanceof NodeChecker) {
            nodeCheckers.remove(checker);
        }
        if (checker instanceof LevelChecker) {
            levelCheckers.remove(checker);
        }

    }

}
