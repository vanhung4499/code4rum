package com.hnv99.forum.service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.hnv99.forum.service")
@MapperScan(basePackages = {
        "com.hnv99.forum.service.article.repository.mapper",
        "com.hnv99.forum.service.user.repository.mapper",
        "com.hnv99.forum.service.comment.repository.mapper",
        "com.hnv99.forum.service.config.repository.mapper",
        "com.hnv99.forum.service.statistics.repository.mapper",
        "com.hnv99.forum.service.notify.repository.mapper",})
public class ServiceAutoConfig {
}
