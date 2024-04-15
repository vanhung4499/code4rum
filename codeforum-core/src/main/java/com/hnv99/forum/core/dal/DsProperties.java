package com.hnv99.forum.core.dal;

import lombok.Data;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * Configuration loading for multiple data sources
 */
@Data
@ConfigurationProperties(prefix = DsProperties.DS_PREFIX)
public class DsProperties {
    public static final String DS_PREFIX = "spring.dynamic";
    /**
     * Default data source
     */
    private String primary;

    /**
     * Multiple data source configuration
     */
    private Map<String, DataSourceProperties> datasource;
}
