package com.hnv99.forum.core.sensitive.ano;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface SensitiveField {
    /**
     * Which field in the database to bind to
     *
     * @return
     */
    String bind() default "";
}
