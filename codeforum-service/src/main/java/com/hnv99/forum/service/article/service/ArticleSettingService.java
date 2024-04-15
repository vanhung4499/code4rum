package com.hnv99.forum.service.article.service;


import com.hnv99.forum.api.model.enums.OperateArticleEnum;
import com.hnv99.forum.api.model.vo.PageVo;
import com.hnv99.forum.api.model.vo.article.ArticlePostReq;
import com.hnv99.forum.api.model.vo.article.SearchArticleReq;
import com.hnv99.forum.api.model.vo.article.dto.ArticleAdminDTO;

/**
 * Article backend interface
 */
public interface ArticleSettingService {

    /**
     * Update an article
     *
     * @param req
     */
    void updateArticle(ArticlePostReq req);

    /**
     * Get the list of articles
     *
     * @param req
     * @return
     */
    PageVo<ArticleAdminDTO> getArticleList(SearchArticleReq req);

    /**
     * Delete an article
     *
     * @param articleId
     */
    void deleteArticle(Long articleId);

    /**
     * Operate on an article
     *
     * @param articleId
     * @param operate
     */
    void operateArticle(Long articleId, OperateArticleEnum operate);
}


