package com.hnv99.forum.service.user.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hnv99.forum.api.model.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User login table
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class UserDO extends BaseDO {

    private static final long serialVersionUID = 1L;

    /**
     * Third-party user ID
     */
    private String thirdAccountId;

    /**
     * Login method: 0-Credentials, 1-Google login, 2-GitHub login
     */
    private Integer loginType;

    /**
     * Delete flag
     */
    private Integer deleted;

    /**
     * Login username
     */
    private String userName;

    /**
     * Login password, stored in ciphertext
     */
    private String password;

}
