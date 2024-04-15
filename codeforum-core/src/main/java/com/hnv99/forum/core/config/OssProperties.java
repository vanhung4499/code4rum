package com.hnv99.forum.core.config;

import lombok.Data;

@Data
public class OssProperties {
    /**
     * Prefix path for uploading files
     */
    private String prefix;
    /**
     * OSS type
     */
    private String type;
    /**
     * The following parameters are configurations for OSS
     */
    private String endpoint;
    private String ak;
    private String sk;
    private String bucket;
    private String host;
}
