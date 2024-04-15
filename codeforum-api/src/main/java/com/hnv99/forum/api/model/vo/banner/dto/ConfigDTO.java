package com.hnv99.forum.api.model.vo.banner.dto;

import com.hnv99.forum.api.model.entity.BaseDTO;
import com.hnv99.forum.api.model.enums.ConfigTagEnum;
import lombok.Data;

/**
 * DTO for banner configuration.
 */
@Data
public class ConfigDTO extends BaseDTO {

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
     * Status of the configuration:
     * 0 - Not published
     * 1 - Published
     */
    private Integer status;

    /**
     * Additional information in JSON format.
     */
    private String extra;

    /**
     * Tags associated with the configuration, separated by commas.
     * For example: "hot, recommended, featured"
     *
     * @see ConfigTagEnum#getCode()
     */
    private String tags;
}

