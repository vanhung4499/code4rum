package com.hnv99.forum.core.autoconf;

import com.alibaba.fastjson2.util.AnnotationUtils;
import com.google.common.collect.Maps;
import com.hnv99.forum.core.util.JsonUtil;
import com.hnv99.forum.core.util.SpringUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * Factory class for custom configuration, specifically used for loading configuration from the ConfDot property file,
 * supporting retrieval from custom configuration sources.
 *
 * author: YiHui
 * date: 2023/6/20
 */
@Slf4j
@Component
public class DynamicConfigContainer implements EnvironmentAware, ApplicationContextAware, CommandLineRunner {
    private ConfigurableEnvironment environment;
    private ApplicationContext applicationContext;
    /**
     * Stores global configurations from the database, with the highest priority.
     */
    @Getter
    public Map<String, Object> cache;

    private DynamicConfigBinder binder;

    /**
     * Callback tasks for configuration changes.
     */
    @Getter
    private Map<Class, Runnable> refreshCallback = Maps.newHashMap();

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        cache = Maps.newHashMap();
        bindBeansFromLocalCache("dbConfig", cache);
    }

    /**
     * Retrieves all configurations from the database.
     *
     * @return true if there are configuration changes; false otherwise.
     */
    private boolean loadAllConfigFromDb() {
        List<Map<String, Object>> list = SpringUtil.getBean(JdbcTemplate.class).queryForList("select `key`, `value` from global_conf where deleted = 0");
        Map<String, Object> val = Maps.newHashMapWithExpectedSize(list.size());
        for (Map<String, Object> conf : list) {
            val.put(conf.get("key").toString(), conf.get("value").toString());
        }
        if (val.equals(cache)) {
            return false;
        }
        cache.clear();
        cache.putAll(val);
        return true;
    }

    private void bindBeansFromLocalCache(String namespace, Map<String, Object> cache) {
        // Set in-memory configuration with the highest priority
        MapPropertySource propertySource = new MapPropertySource(namespace, cache);
        environment.getPropertySources().addFirst(propertySource);
        this.binder = new DynamicConfigBinder(this.applicationContext, environment.getPropertySources());
    }

    /**
     * Binds configuration.
     *
     * @param bindable The bindable object.
     */
    public void bind(Bindable bindable) {
        binder.bind(bindable);
    }


    /**
     * Listens for configuration changes.
     */
    public void reloadConfig() {
        String before = JsonUtil.toStr(cache);
        boolean toRefresh = loadAllConfigFromDb();
        if (toRefresh) {
            refreshConfig();
            log.info("Configuration refreshed! Old: {}, New: {}", before, JsonUtil.toStr(cache));
        }
    }

    /**
     * Forces cache configuration refresh.
     */
    public void forceRefresh() {
        loadAllConfigFromDb();
        refreshConfig();
        log.info("Database configuration force refresh! {}", JsonUtil.toStr(cache));
    }

    /**
     * Supports dynamic configuration refresh.
     */
    private void refreshConfig() {
        applicationContext.getBeansWithAnnotation(ConfigurationProperties.class).values().forEach(bean -> {
            Bindable<?> target = Bindable.ofInstance(bean).withAnnotations(AnnotationUtils.findAnnotation(bean.getClass(), ConfigurationProperties.class));
            bind(target);
            if (refreshCallback.containsKey(bean.getClass())) {
                refreshCallback.get(bean.getClass()).run();
            }
        });
    }

    /**
     * Registers the task for refreshing configuration changes.
     */
    @Scheduled(fixedRate = 300000) // 5 minutes expressed in milliseconds
    private void registerConfRefreshTask() {
        try {
            log.debug("Every 5 minutes, automatically update database configuration information!");
            reloadConfig();
        } catch (Exception e) {
            log.warn("Exception occurred while automatically updating database configuration information!", e);
        }
    }

    /**
     * Registers the callback task for configuration changes.
     *
     * @param bean The bean object.
     * @param run  The runnable task.
     */
    public void registerRefreshCallback(Object bean, Runnable run) {
        refreshCallback.put(bean.getClass(), run);
    }


    /**
     * Dynamically initializes configuration after application startup.
     *
     * @param args The application arguments.
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        reloadConfig();
        registerConfRefreshTask();
    }
}
