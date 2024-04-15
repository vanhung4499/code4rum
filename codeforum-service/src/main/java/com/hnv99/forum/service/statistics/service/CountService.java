package com.hnv99.forum.service.statistics.service;

import com.hnv99.forum.api.model.vo.user.dto.ArticleFootCountDTO;
import com.hnv99.forum.api.model.vo.user.dto.UserStatisticInfoDTO;

/**
 * Counting related service
 */
public interface CountService {
    /**
     * Query article count information by article ID
     * This method directly queries related information based on the database,
     * replace it with the queryArticleStatisticInfo() method below.
     *
     * @param articleId the article ID
     * @return the article count information
     */
    @Deprecated
    ArticleFootCountDTO queryArticleCountInfoByArticleId(Long articleId);


    /**
     * Query user's total reading related counts (currently does not return the comment count)
     * This method directly queries related information based on the database,
     * replace it with the queryUserStatisticInfo() method below.
     *
     * @param userId the user ID
     * @return the user's reading related counts
     */
    @Deprecated
    ArticleFootCountDTO queryArticleCountInfoByUserId(Long userId);

    /**
     * Get the number of likes for a comment
     *
     * @param commentId the comment ID
     * @return the number of likes for the comment
     */
    Long queryCommentPraiseCount(Long commentId);


    /**
     * Query user's related statistical information
     *
     * @param userId the user ID
     * @return the user's statistics including collections, likes, articles, fans, follows, and total article reads
     */
    UserStatisticInfoDTO queryUserStatisticInfo(Long userId);

    /**
     * Query article related statistical information
     *
     * @param articleId the article ID
     * @return the article's statistics including collections, likes, comments, and reads
     */
    ArticleFootCountDTO queryArticleStatisticInfo(Long articleId);


    /**
     * Increment article read count
     *
     * @param authorUserId the author's user ID
     * @param articleId the article ID
     */
    void incrArticleReadCount(Long authorUserId, Long articleId);

    /**
     * Refresh user's statistical information
     *
     * @param userId the user ID
     */
    void refreshUserStatisticInfo(Long userId);

    /**
     * Refresh article's statistical information
     *
     * @param articleId the article ID
     */
    void refreshArticleStatisticInfo(Long articleId);
}

