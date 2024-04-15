package com.hnv99.forum.api.model.vo.user;

import lombok.Data;

/**
 * User information input parameters.
 */
@Data
public class UserInfoSaveReq {

    /**
     * User ID
     */
    private Long userId;

    /**
     * Username
     */
    private String userName;

    /**
     * User photo
     */
    private String photo;

    /**
     * Position
     */
    private String position;

    /**
     * Company
     */
    private String company;

    /**
     * Personal profile
     */
    private String profile;
}

