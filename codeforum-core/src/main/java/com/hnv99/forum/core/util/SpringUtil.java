package com.hnv99.forum.core.util;

import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Utility class for accessing Spring application context and environment.
 */
@Component
public class SpringUtil implements ApplicationContextAware, EnvironmentAware {
    private volatile static ApplicationContext context;
    private volatile static Environment environment;

    private static Binder binder;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtil.context = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        SpringUtil.environment = environment;
        binder = Binder.get(environment);
    }

    public static ApplicationContext getContext() {
        return context;
    }

    /**
     * Get a bean of the specified type.
     *
     * @param bean the type of the bean to retrieve
     * @param <T>  the type of the bean
     * @return an instance of the bean
     */
    public static <T> T getBean(Class<T> bean) {
        return context.getBean(bean);
    }

    /**
     * Get a bean of the specified type, or return null if not found.
     *
     * @param bean the type of the bean to retrieve
     * @param <T>  the type of the bean
     * @return an instance of the bean, or null if not found
     */
    public static <T> T getBeanOrNull(Class<T> bean) {
        try {
            return context.getBean(bean);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get a bean with the specified name.
     *
     * @param beanName the name of the bean to retrieve
     * @return an instance of the bean
     */
    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }

    /**
     * Get a bean with the specified name, or return null if not found.
     *
     * @param beanName the name of the bean to retrieve
     * @return an instance of the bean, or null if not found
     */
    public static Object getBeanOrNull(String beanName) {
        try {
            return context.getBean(beanName);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get the value of a configuration property.
     *
     * @param key the key of the configuration property
     * @return the value of the configuration property
     */
    public static String getConfig(String key) {
        return environment.getProperty(key);
    }

    /**
     * Get the value of a configuration property, or return the value of another property if the first one is not found.
     *
     * @param mainKey  the key of the main configuration property
     * @param slaveKey the key of the slave configuration property
     * @return the value of the main configuration property, or the value of the slave configuration property if the main one is not found
     */
    public static String getConfigOrElse(String mainKey, String slaveKey) {
        String ans = environment.getProperty(mainKey);
        if (ans == null) {
            return environment.getProperty(slaveKey);
        }
        return ans;
    }

    /**
     * Get the value of a configuration property, or return a default value if not found.
     *
     * @param key the key of the configuration property
     * @param val the default value
     * @return the value of the configuration property, or the default value if not found
     */
    public static String getConfig(String key, String val) {
        return environment.getProperty(key, val);
    }

    /**
     * Publish an application event.
     *
     * @param event the event to publish
     */
    public static void publishEvent(ApplicationEvent event) {
        context.publishEvent(event);
    }

    /**
     * Get the configuration binder.
     *
     * @return the configuration binder
     */
    public static Binder getBinder() {
        return binder;
    }
}

