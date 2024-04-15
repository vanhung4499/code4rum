package com.hnv99.forum.service.config.repository.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hnv99.forum.api.model.entity.BaseDO;
import com.hnv99.forum.api.model.enums.ConfigTagEnum;
import com.hnv99.forum.api.model.enums.ConfigTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Entity class representing the configuration table.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("config")
public class ConfigDO extends BaseDO {
    private static final long serialVersionUID = -6122208316544171303L;

    /**
     * Type of the configuration.
     * @see ConfigTypeEnum#getCode()
     */
    private Integer type;

    /**
     * Name of the configuration.
     */
    @TableField("`name`")
    private String name;

    /**
     * URL of the banner image.
     */
    private String bannerUrl;

    /**
     * URL to which the banner should redirect.
     */
    private String jumpUrl;

    /**
     * Content of the configuration.
     */
    private String content;

    /**
     * Sorting order of the configuration.
     */
    @TableField("`rank`")
    private Integer rank;

    /**
     * Status of the configuration: 0 for unpublished, 1 for published.
     */
    private Integer status;

    /**
     * Flag indicating whether the configuration is deleted: 0 for not deleted, 1 for deleted.
     */
    private Integer deleted;

    /**
     * Tags associated with the configuration, separated by commas.
     * @see ConfigTagEnum#getCode()
     */
    private String tags;

    /**
     * Additional information related to the configuration, such as ratings, number of views, and number of downloads.
     */
    private String extra;
}
