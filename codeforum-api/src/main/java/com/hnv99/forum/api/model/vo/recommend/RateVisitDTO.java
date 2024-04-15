package com.hnv99.forum.api.model.vo.recommend;

import lombok.Data;

/**
 * Data transfer object for resource access and rating information.
 */
@Data
public class RateVisitDTO {

    /**
     * Number of views.
     */
    private Integer visit;

    /**
     * Number of downloads.
     */
    private Integer download;

    /**
     * Rating, represented as a floating-point number stored as a string to avoid precision issues.
     */
    private String rate;

    /**
     * Default constructor initializing visit count to 0, download count to 0, and rating to "8".
     */
    public RateVisitDTO() {
        visit = 0;
        download = 0;
        rate = "8";
    }

    /**
     * Increment the visit count by 1.
     */
    public void incVisit() {
        visit += 1;
    }

    /**
     * Increment the download count by 1.
     */
    public void incDownload() {
        download += 1;
    }
}

