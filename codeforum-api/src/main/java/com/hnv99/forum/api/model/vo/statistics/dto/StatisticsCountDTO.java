package com.hnv99.forum.api.model.vo.statistics.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Statistics Count
 */
@Data
@Builder
public class StatisticsCountDTO {

    /**
     * PV count
     */
    private Long pvCount;

    /**
     * Total user count
     */
    private Long userCount;

    /**
     * Total comment count
     */
    private Long commentCount;

    /**
     * Total read count
     */
    private Long readCount;

    /**
     * Total like count
     */
    private Long likeCount;

    /**
     * Total collect count
     */
    private Long collectCount;

    /**
     * Number of articles
     */
    private Long articleCount;

    /**
     * Number of tutorials
     */
    private Long tutorialCount;

    /**
     * Number of star payers
     */
    private Integer starPayCount;
}

