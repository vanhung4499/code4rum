package com.hnv99.forum.core.async;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import lombok.extern.slf4j.Slf4j;

/**
 * Aspect for asynchronous execution
 *
 * This aspect handles the execution of methods annotated with @AsyncExecute, allowing them to be executed asynchronously with a timeout.
 */
@Slf4j
@Aspect
@Component
public class AsyncExecuteAspect implements ApplicationContextAware {

    private ExpressionParser parser;
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.parser = new SpelExpressionParser();
        this.applicationContext = applicationContext;
    }

    /**
     * Aspect around advice for handling asynchronous execution with timeouts
     *
     * @param joinPoint    The join point representing the intercepted method
     * @param asyncExecute The AsyncExecute annotation applied to the method
     * @return The result of the method execution
     * @throws Throwable If an error occurs during method execution
     */
    @Around("@annotation(asyncExecute)")
    public Object handle(ProceedingJoinPoint joinPoint, AsyncExecute asyncExecute) throws Throwable {
        if (!asyncExecute.value()) {
            // If asynchronous execution is not supported, proceed synchronously
            return joinPoint.proceed();
        }

        try {
            // Execute the method call with timeout
            return AsyncUtil.callWithTimeLimit(asyncExecute.timeOut(), asyncExecute.unit(), () -> {
                try {
                    return joinPoint.proceed();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            if (StringUtils.hasText(asyncExecute.timeOutRsp())) {
                // If a custom response is provided for timeout, return it
                return defaultRespWhenTimeOut(joinPoint, asyncExecute);
            } else {
                throw e;
            }
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Provides a default response when a method times out
     *
     * @param joinPoint    The join point representing the intercepted method
     * @param asyncExecute The AsyncExecute annotation applied to the method
     * @return The default response for timeout
     */
    private Object defaultRespWhenTimeOut(ProceedingJoinPoint joinPoint, AsyncExecute asyncExecute) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setBeanResolver(new BeanFactoryResolver(this.applicationContext));

        // Use custom response strategy for timeout
        MethodSignature methodSignature = ((MethodSignature) joinPoint.getSignature());
        String[] parameterNames = methodSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }
        log.info("{} execution timed out, returning default result!", methodSignature.getMethod().getName());
        return parser.parseExpression(asyncExecute.timeOutRsp()).getValue(context);
    }
}
