package com.hnv99.forum.service.comment.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hnv99.forum.api.model.entity.BaseDO;
import com.hnv99.forum.core.sensitive.ano.SensitiveField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Comment entity representing comments on articles.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("comment")
public class CommentDO extends BaseDO {

    private static final long serialVersionUID = 1L;

    /**
     * The ID of the article.
     */
    private Long articleId;

    /**
     * The ID of the user who posted the comment.
     */
    private Long userId;

    /**
     * The content of the comment.
     */
    @SensitiveField(bind = "content")
    private String content;

    /**
     * The ID of the parent comment.
     */
    private Long parentCommentId;

    /**
     * The ID of the top-level comment.
     */
    private Long topCommentId;

    /**
     * Flag indicating whether the comment is deleted or not.
     * 0 - not deleted, 1 - deleted
     */
    private Integer deleted;
}

