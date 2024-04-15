package com.hnv99.forum.core.dal;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * Aspect for handling multiple data sources
 */
@Aspect
public class DsAspect {
    /**
     * Pointcut, intercept methods annotated with `DsAno` on classes or methods, used for switching data sources
     */
    @Pointcut("@annotation(com.hnv99.forum.core.dal.DsAno) || @within(com.hnv99.forum.core.dal.DsAno)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        DsAno ds = getDsAno(proceedingJoinPoint);
        try {
            if (ds != null && (StringUtils.isNotBlank(ds.ds()) || ds.value() != null)) {
                // When there is no context, write into the thread context which DB should be used
                DsContextHolder.set(StringUtils.isNoneBlank(ds.ds()) ? ds.ds() : ds.value().name());
            }
            return proceedingJoinPoint.proceed();
        } finally {
            // Clear context information
            if (ds != null) {
                DsContextHolder.reset();
            }
        }
    }

    private DsAno getDsAno(ProceedingJoinPoint proceedingJoinPoint) {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();
        DsAno ds = method.getAnnotation(DsAno.class);
        if (ds == null) {
            // Get annotation on the class
            ds = (DsAno) proceedingJoinPoint.getSignature().getDeclaringType().getAnnotation(DsAno.class);
        }
        return ds;
    }
}
