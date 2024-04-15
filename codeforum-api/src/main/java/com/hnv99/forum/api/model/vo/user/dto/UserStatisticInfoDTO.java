package com.hnv99.forum.api.model.vo.user.dto;

import com.hnv99.forum.api.model.vo.article.dto.YearArticleDTO;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * User homepage information.
 */
@Data
@ToString(callSuper = true)
public class UserStatisticInfoDTO extends BaseUserInfoDTO {

    /**
     * Number of followings
     */
    private Integer followCount;

    /**
     * Number of followers
     */
    private Integer fansCount;

    /**
     * Days joined
     */
    private Integer joinDayCount;

    /**
     * Number of articles published
     */
    private Integer articleCount;

    /**
     * Number of likes on articles
     */
    private Integer praiseCount;

    /**
     * Number of times articles have been read
     */
    private Integer readCount;

    /**
     * Number of times articles have been favorited
     */
    private Integer collectionCount;

    /**
     * Whether the current user is followed
     */
    private Boolean followed;

    /**
     * Percentage of completeness of identity information
     */
    private Integer infoPercent;

    /**
     * Creation history
     */
    private List<YearArticleDTO> yearArticleList;
}

