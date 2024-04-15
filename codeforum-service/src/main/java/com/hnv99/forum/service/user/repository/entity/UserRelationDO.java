package com.hnv99.forum.service.user.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hnv99.forum.api.model.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User relationship table
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_relation")
public class UserRelationDO extends BaseDO {

    private static final long serialVersionUID = 1L;

    /**
     * Main user ID
     */
    private Long userId;

    /**
     * Fan user ID
     */
    private Long followUserId;

    /**
     * Follow status: 0-unfollowed, 1-followed, 2-unfollowed
     */
    private Integer followState;
}

