package com.hnv99.forum.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hnv99.forum.core.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring5.dialect.SpringStandardDialect;
import org.thymeleaf.standard.serializer.IStandardJavaScriptSerializer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

/**
 * Configures XML parsing.
 * Adjusts the serialization format.
 */
@Slf4j
@Configuration
public class ForumWebConfig implements WebMvcConfigurer {

    @Resource
    private TemplateEngine templateEngine;

    /**
     * Initializes the configuration.
     */
    @PostConstruct
    private void init() {
        log.info("PaiWebConfig init...");
        SpringStandardDialect springStandardDialect = CollectionUtils.findValueOfType(templateEngine.getDialects(), SpringStandardDialect.class);
        IStandardJavaScriptSerializer standardJavaScriptSerializer = springStandardDialect.getJavaScriptSerializer();
        Field delegateField = ReflectionUtils.findField(standardJavaScriptSerializer.getClass(), "delegate");
        if (delegateField == null) {
            log.warn("WebConfig init, failed set jackson module, delegateField is null");
            return;
        }
        ReflectionUtils.makeAccessible(delegateField);
        Object delegate = ReflectionUtils.getField(delegateField, standardJavaScriptSerializer);
        if (delegate == null) {
            log.warn("WebConfig init, failed set jackson module, delegateField is null");
            return;
        }
        if (Objects.equals("JacksonStandardJavaScriptSerializer", delegate.getClass().getSimpleName())) {
            Field mapperField = ReflectionUtils.findField(delegate.getClass(), "mapper");
            if (mapperField == null) {
                log.warn("WebConfig init, failed set jackson module, mapperField is null");
                return;
            }
            ReflectionUtils.makeAccessible(mapperField);
            ObjectMapper objectMapper = (ObjectMapper) ReflectionUtils.getField(mapperField, delegate);
            if (objectMapper == null) {
                log.warn("WebConfig init, filed set jackson module, mapper is null");
                return;
            }
            objectMapper.registerModule(JsonUtil.bigIntToStrSimpleModule());
            log.info("WebConfig init set jackson serialization long to string successfully!!!");
        }
    }

    /**
     * Configures the message converters.
     *
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2XmlHttpMessageConverter());
        converters.forEach(s -> {
            if (s instanceof MappingJackson2HttpMessageConverter) {
                ((MappingJackson2HttpMessageConverter) s).getObjectMapper().registerModule(JsonUtil.bigIntToStrSimpleModule());
            }
        });
    }

    /**
     * Configures content negotiation.
     *
     * @param configurer
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorParameter(true)
                .defaultContentType(MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN, MediaType.TEXT_EVENT_STREAM, MediaType.APPLICATION_OCTET_STREAM, MediaType.MULTIPART_FORM_DATA, MediaType.MULTIPART_MIXED, MediaType.MULTIPART_RELATED)
                .ignoreAcceptHeader(true)
                .parameterName("mediaType")
                .mediaType("json", MediaType.APPLICATION_JSON)
                .mediaType("xml", MediaType.APPLICATION_XML)
                .mediaType("html", MediaType.TEXT_HTML)
                .mediaType("text", MediaType.TEXT_PLAIN)
                .mediaType("text/event-stream", MediaType.TEXT_EVENT_STREAM)
                .mediaType("application/octet-stream", MediaType.APPLICATION_OCTET_STREAM)
                .mediaType("multipart/form-data", MediaType.MULTIPART_FORM_DATA);
    }
}

