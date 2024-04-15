package com.hnv99.forum.core.util;

import org.springframework.util.StopWatch;

import java.util.concurrent.Callable;

/**
 * Utility class for measuring execution time
 */
public class StopWatchUtil {
    private StopWatch stopWatch;

    /**
     * Private constructor
     *
     * @param task Name of the task
     */
    private StopWatchUtil(String task) {
        stopWatch = task == null ? new StopWatch() : new StopWatch(task);
    }

    /**
     * Initialize the stopwatch
     *
     * @param task Task name
     * @return StopWatchUtil instance
     */
    public static StopWatchUtil init(String... task) {
        return new StopWatchUtil(task.length > 0 ? task[0] : null);
    }

    /**
     * Record the elapsed time for a task
     *
     * @param task Task name
     * @param call Callable containing the business logic
     * @param <T>  Return type
     * @return Result of the callable
     */
    public <T> T record(String task, Callable<T> call) {
        stopWatch.start(task);
        try {
            return call.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            stopWatch.stop();
        }
    }

    /**
     * Record the elapsed time for a task
     *
     * @param task Task name
     * @param run  Runnable containing the business logic
     */
    public void record(String task, Runnable run) {
        stopWatch.start(task);
        try {
            run.run();
        } finally {
            stopWatch.stop();
        }
    }

    /**
     * Output timing information in a human-readable format
     *
     * @return String representation of timing information
     */
    public String prettyPrint() {
        return stopWatch.prettyPrint();
    }
}

