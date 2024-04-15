package com.hnv99.forum.api.model.vo.article;

import lombok.Data;

import java.io.Serializable;

/**
 * Save Column Request Parameters
 */
@Data
public class ColumnReq implements Serializable {

    /**
     * ID
     */
    private Long columnId;

    /**
     * Column Name
     */
    private String column;

    /**
     * Author
     */
    private Long author;

    /**
     * Introduction
     */
    private String introduction;

    /**
     * Cover
     */
    private String cover;

    /**
     * Status
     */
    private Integer state;

    /**
     * Sorting
     */
    private Integer section;

    /**
     * Expected number of articles in the column
     */
    private Integer nums;

    /**
     * Column Type
     */
    private Integer type;

    /**
     * Start time of limited-time free access
     */
    private Long freeStartTime;

    /**
     * End time of limited-time free access
     */
    private Long freeEndTime;
}

