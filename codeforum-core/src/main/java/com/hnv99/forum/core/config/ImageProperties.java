package com.hnv99.forum.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Image configuration file
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "image")
public class ImageProperties {

    /**
     * Absolute storage path
     */
    private String absTmpPath;

    /**
     * Relative storage path
     */
    private String webImgPath;

    /**
     * Temporary storage directory for uploaded files
     */
    private String tmpUploadPath;

    /**
     * Host for accessing images
     */
    private String cdnHost;

    private OssProperties oss;

    public String buildImgUrl(String url) {
        if (!url.startsWith(cdnHost)) {
            return cdnHost + url;
        }
        return url;
    }
}

