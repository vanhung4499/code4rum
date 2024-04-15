package com.hnv99.forum.api.model.vo.user.dto;

import lombok.Data;
import lombok.ToString;

/**
 * DTO representing user homepage information.
 */
@Data
@ToString(callSuper = true)
public class UserFootStatisticDTO {

    /**
     * Number of article likes
     */
    private Long praiseCount;

    /**
     * Number of times articles were read
     */
    private Long readCount;

    /**
     * Number of times articles were bookmarked
     */
    private Long collectionCount;

    /**
     * Number of article comments
     */
    private Long commentCount;

    /**
     * Default constructor initializing counts to zero
     */
    public UserFootStatisticDTO() {
        praiseCount = 0L;
        readCount = 0L;
        collectionCount = 0L;
        commentCount = 0L;
    }
}
