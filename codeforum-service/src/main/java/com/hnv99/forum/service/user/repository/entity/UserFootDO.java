package com.hnv99.forum.service.user.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hnv99.forum.api.model.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User footprint table
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_foot")
public class UserFootDO extends BaseDO {

    private static final long serialVersionUID = 1L;

    /**
     * User ID
     */
    private Long userId;

    /**
     * Document ID (article/comment)
     */
    private Long documentId;

    /**
     * Document type: 1-article, 2-comment
     */
    private Integer documentType;

    /**
     * User ID who published the document
     */
    private Long documentUserId;

    /**
     * Collection status: 0-not collected, 1-collected
     */
    private Integer collectionStat;

    /**
     * Read status: 0-unread, 1-read
     */
    private Integer readStat;

    /**
     * Comment status: 0-not commented, 1-commented
     */
    private Integer commentStat;

    /**
     * Like status: 0-not liked, 1-liked
     */
    private Integer praiseStat;
}

