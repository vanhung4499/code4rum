package com.hnv99.forum;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hnv99.forum.config.GlobalViewConfig;
import com.hnv99.forum.core.util.SocketUtil;
import com.hnv99.forum.core.util.SpringUtil;
import com.hnv99.forum.global.ForumExceptionHandler;
import com.hnv99.forum.hook.interceptor.GlobalViewInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.List;

/**
 * Entry point, run directly to start the application.
 */
@Slf4j
@EnableAsync
@EnableScheduling
@EnableCaching
@ServletComponentScan
@SpringBootApplication
public class QuickForumApplication implements WebMvcConfigurer, ApplicationRunner {
    @Value("${server.port:8080}")
    private Integer webPort;

    @Resource
    private GlobalViewInterceptor globalViewInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(globalViewInterceptor).addPathPatterns("/**");
    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add(0, new ForumExceptionHandler());
    }

    public static void main(String[] args) {
        SpringApplication.run(QuickForumApplication.class, args);
    }

    /**
     * Compatible with the scenario where port 8080 is occupied when starting locally;
     * Only do this logic when starting in dev mode.
     *
     * @return TomcatConnectorCustomizer
     */
    @Bean
    @ConditionalOnExpression(value = "#{'dev'.equals(environment.getProperty('env.name'))}")
    public TomcatConnectorCustomizer customServerPortTomcatConnectorCustomizer() {
        // In the development environment, first check if port 8080 is available;
        // If available, use it directly, otherwise choose an available port to start.
        int port = SocketUtil.findAvailableTcpPort(8000, 10000, webPort);
        if (port != webPort) {
            log.info("Default port {} is occupied, randomly using new port: {}", webPort, port);
            webPort = port;
        }
        return connector -> connector.setPort(port);
    }

    @Override
    public void run(ApplicationArguments args) {
        // Set up type conversion, mainly used for mybatis to read varchar/json type data and write it to json format entities.
        JacksonTypeHandler.setObjectMapper(new ObjectMapper());
        // Executed after the application starts.
        GlobalViewConfig config = SpringUtil.getBean(GlobalViewConfig.class);
        if (webPort != null) {
            config.setHost("http://127.0.0.1:" + webPort);
        }
        log.info("Application started successfully, click to enter the homepage: {}", config.getHost());
    }
}

