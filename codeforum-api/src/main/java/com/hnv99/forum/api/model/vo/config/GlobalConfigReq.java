package com.hnv99.forum.api.model.vo.config;

import lombok.Data;

/**
 * Request class for global configuration.
 */
@Data
public class GlobalConfigReq {
    // Name of the configuration item
    private String keywords;
    // Value of the configuration item
    private String value;
    // Remark or comment
    private String comment;
    // ID
    private Long id;
}

