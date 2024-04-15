package com.hnv99.forum.service.article.service;

import com.hnv99.forum.api.model.vo.article.ArticlePostReq;

public interface ArticleWriteService {

    /**
     * Save or update an article.
     *
     * @param req    The article body to upload.
     * @param author The author of the article.
     * @return The primary key of the article.
     */
    Long saveArticle(ArticlePostReq req, Long author);

    /**
     * Delete an article.
     *
     * @param articleId   The ID of the article to delete.
     * @param loginUserId The user performing the operation.
     */
    void deleteArticle(Long articleId, Long loginUserId);
}

