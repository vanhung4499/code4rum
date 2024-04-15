package com.hnv99.forum.service.image.oss.impl;

import com.hnv99.forum.core.config.ImageProperties;
import com.hnv99.forum.core.net.HttpRequestHelper;
import com.hnv99.forum.core.util.JsonUtil;
import com.hnv99.forum.core.util.StopWatchUtil;
import com.hnv99.forum.service.image.oss.ImageUploader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP-based file upload
 */
@Slf4j
@Component
@ConditionalOnExpression(value = "#{'rest'.equals(environment.getProperty('image.oss.type'))}")
public class RestOssWrapper implements ImageUploader {
    @Autowired
    private ImageProperties properties;

    @Override
    public String upload(InputStream input, String fileType) {
        StopWatchUtil stopWatchUtil = StopWatchUtil.init("Image upload");
        try {
            byte[] bytes = stopWatchUtil.record("Convert to bytes", () -> StreamUtils.copyToByteArray(input));
            String res = stopWatchUtil.record("Upload", () -> HttpRequestHelper.upload(properties.getOss().getEndpoint(), "image", "img." + fileType, bytes));
            HashMap<?, ?> map = JsonUtil.toObj(res, HashMap.class);
            return (String) ((Map<?, ?>) map.get("result")).get("imagePath");
        } catch (Exception e) {
            log.error("Upload image error response! URI:{}", properties.getOss().getEndpoint(), e);
            return null;
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("Upload Image cost: {}", stopWatchUtil.prettyPrint());
            }
        }
    }

    @Override
    public boolean uploadIgnore(String fileUrl) {
        if (StringUtils.isNotBlank(properties.getOss().getHost()) && fileUrl.startsWith(properties.getOss().getHost())) {
            return true;
        }
        return !fileUrl.startsWith("http");
    }
}

