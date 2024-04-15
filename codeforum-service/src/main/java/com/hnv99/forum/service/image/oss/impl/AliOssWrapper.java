package com.hnv99.forum.service.image.oss.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.hnv99.forum.core.autoconf.DynamicConfigContainer;
import com.hnv99.forum.core.config.ImageProperties;
import com.hnv99.forum.core.util.Md5Util;
import com.hnv99.forum.core.util.StopWatchUtil;
import com.hnv99.forum.service.image.oss.ImageUploader;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Alibaba Cloud OSS File Uploader
 */
@Slf4j
@ConditionalOnExpression(value = "#{'ali'.equals(environment.getProperty('image.oss.type'))}")
@Component
public class AliOssWrapper implements ImageUploader, InitializingBean, DisposableBean {

    /**
     * Success response code
     */
    private static final int SUCCESS_CODE = 200;

    /**
     * Image properties
     */
    @Autowired
    @Setter
    @Getter
    private ImageProperties properties;

    /**
     * OSS client
     */
    private OSS ossClient;

    /**
     * Dynamic configuration container
     */
    @Autowired
    private DynamicConfigContainer dynamicConfigContainer;

    /**
     * Upload a file from an input stream
     *
     * @param input    InputStream of the file
     * @param fileType File type
     * @return URL of the uploaded file
     */
    public String upload(InputStream input, String fileType) {
        try {
            byte[] bytes = StreamUtils.copyToByteArray(input);
            return upload(bytes, fileType);
        } catch (OSSException oe) {
            log.error("OSS rejected with an error response! msg:{}, code:{}, reqId:{}, host:{}", oe.getErrorMessage(), oe.getErrorCode(), oe.getRequestId(), oe.getHostId());
            return "";
        } catch (Exception ce) {
            log.error("Caught a ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network. {}", ce.getMessage());
            return "";
        }
    }

    /**
     * Upload a file from byte array
     *
     * @param bytes    Byte array of the file
     * @param fileType File type
     * @return URL of the uploaded file
     */
    public String upload(byte[] bytes, String fileType) {
        StopWatchUtil stopWatchUtil = StopWatchUtil.init("Image Upload");
        try {
            String fileName = stopWatchUtil.record("MD5 Calculation", () -> Md5Util.encode(bytes));
            ByteArrayInputStream input = new ByteArrayInputStream(bytes);
            fileName = properties.getOss().getPrefix() + fileName + "." + getFileType(input, fileType);
            PutObjectRequest putObjectRequest = new PutObjectRequest(properties.getOss().getBucket(), fileName, input);
            putObjectRequest.setProcess("true");
            PutObjectResult result = stopWatchUtil.record("File Upload", () -> ossClient.putObject(putObjectRequest));
            if (SUCCESS_CODE == result.getResponse().getStatusCode()) {
                return properties.getOss().getHost() + fileName;
            } else {
                log.error("Upload to OSS error! response:{}", result.getResponse().getStatusCode());
                return "";
            }
        } catch (OSSException oe) {
            log.error("OSS rejected with an error response! msg:{}, code:{}, reqId:{}, host:{}", oe.getErrorMessage(), oe.getErrorCode(), oe.getRequestId(), oe.getHostId());
            return "";
        } catch (Exception ce) {
            log.error("Caught a ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network. {}", ce.getMessage());
            return "";
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("Upload image size:{} cost: {}", bytes.length, stopWatchUtil.prettyPrint());
            }
        }
    }

    /**
     * Determine if an external image URL needs processing
     *
     * @param fileUrl External image URL
     * @return true if the URL should be ignored and not processed
     */
    @Override
    public boolean uploadIgnore(String fileUrl) {
        if (StringUtils.isNotBlank(properties.getOss().getHost()) && fileUrl.startsWith(properties.getOss().getHost())) {
            return true;
        }
        return !fileUrl.startsWith("http");
    }

    /**
     * Destroy method to shutdown the OSS client
     */
    @Override
    public void destroy() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }

    /**
     * Initialize the OSS client
     */
    private void init() {
        log.info("Initializing OSS client");
        ossClient = new OSSClientBuilder().build(properties.getOss().getEndpoint(), properties.getOss().getAk(), properties.getOss().getSk());
    }

    /**
     * Method to be executed after bean properties are set, initializing the OSS client and registering refresh callback
     */
    @Override
    public void afterPropertiesSet() {
        init();
        dynamicConfigContainer.registerRefreshCallback(properties, () -> {
            init();
            log.info("OSS client refreshed!");
        });
    }
}
