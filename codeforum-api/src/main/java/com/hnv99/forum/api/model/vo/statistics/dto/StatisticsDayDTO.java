package com.hnv99.forum.api.model.vo.statistics.dto;

import lombok.Data;

/**
 * Statistics Count for Each Day
 */
@Data
public class StatisticsDayDTO {

    /**
     * Date
     */
    private String date;

    /**
     * Count
     */
    private Long pvCount;

    /**
     * UV count
     */
    private Long uvCount;
}

