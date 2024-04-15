package com.hnv99.forum.api.model.vo.user;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * User input parameters.
 */
@Data
@Accessors(chain = true)
public class UserSaveReq {
    /**
     * Primary key ID
     */
    private Long userId;

    /**
     * Third-party user ID
     */
    private String thirdAccountId;

    /**
     * Login method: 0-Credential login, 1-Google login, 2-GitHub login
     */
    private Integer loginType;
}
