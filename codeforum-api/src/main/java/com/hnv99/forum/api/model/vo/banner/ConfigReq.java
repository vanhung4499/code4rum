package com.hnv99.forum.api.model.vo.banner;

import lombok.Data;

import java.io.Serializable;

/**
 * Request object for saving banner.
 */
@Data
public class ConfigReq implements Serializable {

    /**
     * ID of the configuration.
     */
    private Long configId;

    /**
     * Type of the configuration.
     */
    private Integer type;

    /**
     * Name of the configuration.
     */
    private String name;

    /**
     * URL of the banner image.
     */
    private String bannerUrl;

    /**
     * URL to redirect when clicked.
     */
    private String jumpUrl;

    /**
     * Content of the configuration.
     */
    private String content;

    /**
     * Sorting order of the configuration.
     */
    private Integer rank;

    /**
     * Tags associated with the configuration.
     */
    private String tags;
}

