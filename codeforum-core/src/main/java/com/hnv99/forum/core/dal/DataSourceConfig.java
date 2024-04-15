package com.hnv99.forum.core.dal;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.Map;

/**
 * When multiple data sources are configured, enable this configuration
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "spring.dynamic", name = "primary")
@EnableConfigurationProperties(DsProperties.class)
public class DataSourceConfig {

    private Environment environment;

    public DataSourceConfig(Environment environment) {
        this.environment = environment;
        log.info("Dynamic data source initialization!");
    }

    @Bean
    public DsAspect dsAspect() {
        return new DsAspect();
    }

    @Bean
    public SqlStateInterceptor sqlStateInterceptor() {
        return new SqlStateInterceptor();
    }

    /**
     * Integration of master-slave data sources
     *
     * @param dsProperties
     * @return
     */
    @Bean
    @Primary
    public DataSource dataSource(DsProperties dsProperties) {
        Map<Object, Object> targetDataSources = Maps.newHashMapWithExpectedSize(dsProperties.getDatasource().size());
        dsProperties.getDatasource().forEach((k, v) -> targetDataSources.put(k.toUpperCase(), initDataSource(k, v)));

        if (CollectionUtils.isEmpty(targetDataSources)) {
            throw new IllegalStateException("Multiple data source configuration, please start with spring.dynamic");
        }

        MyRoutingDataSource myRoutingDataSource = new MyRoutingDataSource();
        Object key = dsProperties.getPrimary().toUpperCase();
        if (!targetDataSources.containsKey(key)) {
            if (targetDataSources.containsKey(MasterSlaveDsEnum.MASTER.name())) {
                // When no data source corresponding to primary is configured, and there exists a MASTER data source,
                // the master database will be used as the default data source
                key = MasterSlaveDsEnum.MASTER.name();
            } else {
                key = targetDataSources.keySet().iterator().next();
            }
        }

        log.info("Dynamic data source, default enabled as: " + key);
        myRoutingDataSource.setDefaultTargetDataSource(targetDataSources.get(key));
        myRoutingDataSource.setTargetDataSources(targetDataSources);
        return myRoutingDataSource;
    }

    public DataSource initDataSource(String prefix, DataSourceProperties properties) {
        if (!DruidCheckUtil.hasDuridPkg()) {
            log.info("Instantiating HikariDataSource: {}", prefix);
            return properties.initializeDataSourceBuilder().build();
        }

        if (properties.getType() == null || !properties.getType().isAssignableFrom(DruidDataSource.class)) {
            log.info("Instantiating HikariDataSource: {}", prefix);
            return properties.initializeDataSourceBuilder().build();
        }

        log.info("Instantiating DruidDataSource: {}", prefix);
        // FIXME Knowledge Point: Manually assigning configurations to instances
        return Binder.get(environment).bindOrCreate(DsProperties.DS_PREFIX + ".datasource." + prefix, DruidDataSource.class);
    }

    /**
     * Create after the data source is instantiated
     *
     * @return
     */
    @Bean
    @ConditionalOnExpression(value = "T(com.hnv99.forum.core.dal.DruidCheckUtil).hasDuridPkg()")
    public ServletRegistrationBean<?> druidStatViewServlet() {
        // Configure the management background servlet, the access entry is /druid/
        ServletRegistrationBean<?> servletRegistrationBean = new ServletRegistrationBean<>(
                new StatViewServlet(), "/druid/*");
        // IP whitelist (allow all if not configured or empty)
        servletRegistrationBean.addInitParameter("allow", "127.0.0.1");
        // IP blacklist (deny takes precedence over allow if both exist)
        servletRegistrationBean.addInitParameter("deny", "");
        servletRegistrationBean.addInitParameter("loginUsername", "admin");
        servletRegistrationBean.addInitParameter("loginPassword", "admin");
        servletRegistrationBean.addInitParameter("resetEnable", "false");
        log.info("Enabling druid data source monitoring panel");
        return servletRegistrationBean;
    }
}

