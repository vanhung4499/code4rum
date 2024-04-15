package com.hnv99.forum.api.model.vo.comment;

import lombok.Data;

/**
 * Comment list input parameters
 */
@Data
public class CommentSaveReq {

    /**
     * Comment ID
     */
    private Long commentId;

    /**
     * Article ID
     */
    private Long articleId;

    /**
     * User ID
     */
    private Long userId;

    /**
     * Comment content
     */
    private String commentContent;

    /**
     * Parent comment ID
     */
    private Long parentCommentId;

    /**
     * Top-level comment ID
     */
    private Long topCommentId;
}
