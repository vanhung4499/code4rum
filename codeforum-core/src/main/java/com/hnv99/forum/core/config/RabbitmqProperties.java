package com.hnv99.forum.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RabbitMQ configuration file
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitmqProperties {

    /**
     * Host
     */
    private String host;

    /**
     * Port
     */
    private Integer port;

    /**
     * Username
     */
    private String username;

    /**
     * Password
     */
    private String password;

    /**
     * Path
     */
    private String virtualHost;

    /**
     * Connection pool size
     */
    private Integer poolSize;

    /**
     * Switch false - off, true - on
     */
    private Boolean switchFlag;
}
