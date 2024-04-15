package com.hnv99.forum.api.model.vo.user;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Request information for login using username and password.
 */
@Data
@Accessors(chain = true)
public class UserPwdLoginReq implements Serializable {
    private static final long serialVersionUID = 2139742660700910738L;

    /**
     * User ID
     */
    private Long userId;

    /**
     * Login username
     */
    private String username;

    /**
     * Login password
     */
    private String password;

    /**
     * Planet number
     */
    private String starNumber;

    /**
     * Invitation code
     */
    private String invitationCode;
}

