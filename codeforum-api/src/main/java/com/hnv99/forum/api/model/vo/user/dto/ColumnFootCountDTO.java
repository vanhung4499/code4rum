package com.hnv99.forum.api.model.vo.user.dto;

import lombok.Data;

/**
 * DTO representing statistics for a column.
 */
@Data
public class ColumnFootCountDTO {

    /**
     * Number of likes for the column.
     */
    private Integer praiseCount;

    /**
     * Number of reads for the column.
     */
    private Integer readCount;

    /**
     * Number of times the column has been bookmarked.
     */
    private Integer collectionCount;

    /**
     * Number of comments on the column.
     */
    private Integer commentCount;

    /**
     * Number of articles updated in the column.
     */
    private Integer articleCount;

    /**
     * Total number of articles in the column.
     */
    private Integer totalNums;

    /**
     * Default constructor initializing all counts to 0.
     */
    public ColumnFootCountDTO() {
        praiseCount = 0;
        readCount = 0;
        collectionCount = 0;
        commentCount = 0;
        articleCount = 0;
        totalNums = 0;
    }
}
