package com.hnv99.forum.api.model.vo.user.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * DTO representing follower user information.
 */
@Data
public class FollowUserInfoDTO implements Serializable {
    private static final long serialVersionUID = 7169636386013658631L;

    /**
     * The relationship ID between the current logged-in user and this user.
     */
    private Long relationId;

    /**
     * Indicates whether the current logged-in user is following this user.
     */
    private Boolean followed;

    /**
     * User ID.
     */
    private Long userId;

    /**
     * Username.
     */
    private String userName;

    /**
     * User avatar.
     */
    private String avatar;
}
