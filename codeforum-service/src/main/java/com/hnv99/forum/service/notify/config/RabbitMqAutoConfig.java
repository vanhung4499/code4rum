package com.hnv99.forum.service.notify.config;

import com.hnv99.forum.core.async.AsyncUtil;
import com.hnv99.forum.core.config.RabbitmqProperties;
import com.hnv99.forum.core.rabbitmq.RabbitmqConnectionPool;
import com.hnv99.forum.service.notify.service.RabbitmqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
@ConditionalOnProperty(value = "rabbitmq.switchFlag")
@EnableConfigurationProperties(RabbitmqProperties.class)
public class RabbitMqAutoConfig implements ApplicationRunner {
    @Resource
    private RabbitmqService rabbitmqService;

    @Autowired
    private RabbitmqProperties rabbitmqProperties;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        String host = rabbitmqProperties.getHost();
        Integer port = rabbitmqProperties.getPort();
        String userName = rabbitmqProperties.getUsername();
        String password = rabbitmqProperties.getPassword();
        String virtualhost = rabbitmqProperties.getVirtualHost();
        Integer poolSize = rabbitmqProperties.getPoolSize();
        RabbitmqConnectionPool.initRabbitmqConnectionPool(host, port, userName, password, virtualhost, poolSize);
        AsyncUtil.execute(() -> rabbitmqService.processConsumerMsg());
    }
}
