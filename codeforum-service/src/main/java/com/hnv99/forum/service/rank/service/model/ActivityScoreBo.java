package com.hnv99.forum.service.rank.service.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Activity Score Business Object
 *
 * Represents the activity score for various user actions.
 */
@Data
@Accessors(chain = true)
public class ActivityScoreBo {
    /**
     * Increase activity by visiting a page
     */
    private String path;

    /**
     * Target article
     */
    private Long articleId;

    /**
     * Increase activity by commenting
     */
    private Boolean rate;

    /**
     * Increase activity by liking
     */
    private Boolean praise;

    /**
     * Increase activity by collecting
     */
    private Boolean collect;

    /**
     * Increase activity by publishing an article
     */
    private Boolean publishArticle;

    /**
     * User being followed
     */
    private Long followedUserId;

    /**
     * Increase activity by following
     */
    private Boolean follow;
}
