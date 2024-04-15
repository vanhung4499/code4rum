package com.hnv99.forum.core.permission;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Permission {
    /**
     * Restricted role
     *
     * @return
     */
    UserRole role() default UserRole.ALL;
}
