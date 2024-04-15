package com.hnv99.forum.service.comment.service;

import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.comment.dto.TopCommentDTO;
import com.hnv99.forum.service.comment.repository.entity.CommentDO;

import java.util.List;

/**
 * Comment Read Service Interface
 */
public interface CommentReadService {

    /**
     * Query comment information by comment ID
     *
     * @param commentId The ID of the comment
     * @return The CommentDO object representing the comment
     */
    CommentDO queryComment(Long commentId);

    /**
     * Retrieve a list of comments for an article
     *
     * @param articleId The ID of the article
     * @param page      The PageParam object representing pagination information
     * @return A list of TopCommentDTO objects representing the top-level comments for the article
     */
    List<TopCommentDTO> getArticleComments(Long articleId, PageParam page);

    /**
     * Retrieve the hot comment for an article
     *
     * @param articleId The ID of the article
     * @return The TopCommentDTO object representing the hot comment for the article
     */
    TopCommentDTO queryHotComment(Long articleId);

    /**
     * Retrieve the number of valid comments for an article
     *
     * @param articleId The ID of the article
     * @return The number of valid comments for the article
     */
    int queryCommentCount(Long articleId);
}
