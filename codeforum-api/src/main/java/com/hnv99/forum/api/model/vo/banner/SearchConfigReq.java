package com.hnv99.forum.api.model.vo.banner;

import lombok.Data;

/**
 * Request object for search configuration.
 */
@Data
public class SearchConfigReq {

    /**
     * Type of configuration.
     */
    private Integer type;

    /**
     * Name of the configuration.
     */
    private String name;

    /**
     * Pagination: page number.
     */
    private Long pageNumber;

    /**
     * Pagination: page size.
     */
    private Long pageSize;

}

