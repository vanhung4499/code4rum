package com.hnv99.forum.api.model.vo.config;

import lombok.Data;

/**
 * Request class for global search configuration.
 */
@Data
public class SearchGlobalConfigReq {
    // Name of the configuration item
    private String keywords;
    // Value of the configuration item
    private String value;
    // Remark or comment
    private String comment;
    // Pagination
    private Long pageNumber;
    private Long pageSize;
}

