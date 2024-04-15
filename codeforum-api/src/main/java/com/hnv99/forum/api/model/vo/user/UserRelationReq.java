package com.hnv99.forum.api.model.vo.user;

import lombok.Data;

/**
 * User relationship input parameters.
 */
@Data
public class UserRelationReq {

    /**
     * User ID
     */
    private Long userId;

    /**
     * Follower user ID
     */
    private Long followUserId;

    /**
     * Whether the user is following the current user
     */
    private Boolean followed;
}
