package com.hnv99.forum.core;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.hnv99.forum.core.cache.RedisClient;
import com.hnv99.forum.core.config.ProxyProperties;
import com.hnv99.forum.core.net.ProxyCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties(ProxyProperties.class)
@ComponentScan(basePackages = "com.hnv99.forum.core")
public class ForumCoreAutoConfig {
    @Autowired
    private ProxyProperties proxyProperties;

    public ForumCoreAutoConfig(RedisTemplate<String, String> redisTemplate) {
        RedisClient.register(redisTemplate);
    }

    /**
     * Define the cache manager for use with Spring's @Cache annotation
     *
     * @return
     */
    @Bean("caffeineCacheManager")
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder().
                // Set expiration time, expire after five minutes after writing
                        expireAfterWrite(5, TimeUnit.MINUTES)
                // Initialize the cache space size
                .initialCapacity(100)
                // Maximum number of cache entries
                .maximumSize(200)
        );
        return cacheManager;
    }

    @PostConstruct
    public void init() {
        // Here, manually parse the configuration information and instantiate it as Java POJO objects to initialize the proxy pool
        ProxyCenter.initProxyPool(proxyProperties.getProxy());
    }

}
