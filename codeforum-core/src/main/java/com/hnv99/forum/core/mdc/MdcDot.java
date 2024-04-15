package com.hnv99.forum.core.mdc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for MDC (Mapped Diagnostic Context) dot integration.
 * This annotation can be applied to methods or types (classes).
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MdcDot {
    /**
     * Specify the business code associated with this MDC dot.
     *
     * @return the business code
     */
    String bizCode() default "";
}
