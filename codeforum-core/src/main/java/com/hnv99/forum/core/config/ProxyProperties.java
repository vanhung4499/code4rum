package com.hnv99.forum.core.config;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.Proxy;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "net")
public class ProxyProperties {
    private List<ProxyType> proxy;

    @Data
    @Accessors(chain = true)
    public static class ProxyType {
        /**
         * Proxy type
         */
        private Proxy.Type type;
        /**
         * Proxy IP address
         */
        private String ip;
        /**
         * Proxy port
         */
        private Integer port;
    }
}
