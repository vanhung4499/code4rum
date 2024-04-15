package com.hnv99.forum.core.async;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * Annotation for asynchronous execution
 *
 * This annotation can be applied to methods to enable asynchronous execution with a timeout. It allows configuring whether to enable asynchronous execution, the timeout duration, the timeout unit, and a fallback logic in case of timeout.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AsyncExecute {
    /**
     * Whether to enable asynchronous execution
     *
     * @return true if asynchronous execution is enabled, false otherwise
     */
    boolean value() default true;

    /**
     * Timeout duration in seconds, default is 3 seconds
     *
     * @return the timeout duration
     */
    int timeOut() default 3;

    /**
     * Timeout unit, default is seconds
     *
     * @return the timeout unit
     */
    TimeUnit unit() default TimeUnit.SECONDS;

    /**
     * Fallback logic to be executed when a timeout occurs, supports SpEL
     * If an empty string is returned, it indicates throwing an exception
     *
     * @return the fallback logic
     */
    String timeOutRsp() default "";
}
