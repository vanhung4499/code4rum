package com.hnv99.forum.service.article.service;

import com.hnv99.forum.api.model.enums.HomeSelectEnum;
import com.hnv99.forum.api.model.vo.PageListVo;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.PageVo;
import com.hnv99.forum.api.model.vo.article.dto.ArticleDTO;
import com.hnv99.forum.api.model.vo.article.dto.SimpleArticleDTO;
import com.hnv99.forum.api.model.vo.article.dto.TagDTO;
import com.hnv99.forum.service.article.repository.entity.ArticleDO;

import java.util.List;
import java.util.Map;

public interface ArticleReadService {

    /**
     * Query basic article information
     *
     * @param articleId
     * @return
     */
    ArticleDO queryBasicArticle(Long articleId);

    /**
     * Generate article summary
     *
     * @param content
     * @return
     */
    String generateSummary(String content);

    /**
     * Query article tag list
     *
     * @param articleId
     * @return
     */
    PageVo<TagDTO> queryTagsByArticleId(Long articleId);

    /**
     * Query article details, including main content, category, tags, etc.
     *
     * @param articleId
     * @return
     */
    ArticleDTO queryDetailArticleInfo(Long articleId);

    /**
     * Query all related information of an article, including main content, category, tags, reading count +1, whether the current logged-in user has liked or commented
     *
     * @param articleId   Article ID
     * @param currentUser Current user ID viewing the article
     * @return
     */
    ArticleDTO queryFullArticleInfo(Long articleId, Long currentUser);

    /**
     * Query articles under a certain category, supporting pagination
     *
     * @param categoryId
     * @param page
     * @return
     */
    PageListVo<ArticleDTO> queryArticlesByCategory(Long categoryId, PageParam page);


    /**
     * Get Top articles
     *
     * @param categoryId
     * @return
     */
    List<ArticleDTO> queryTopArticlesByCategory(Long categoryId);


    /**
     * Get the number of articles in a category
     *
     * @param categoryId
     * @return
     */
    Long queryArticleCountByCategory(Long categoryId);

    /**
     * Count articles by category
     *
     * @return
     */
    Map<Long, Long> queryArticleCountsByCategory();

    /**
     * Query articles under a certain tag, supporting pagination
     *
     * @param tagId
     * @param page
     * @return
     */
    PageListVo<ArticleDTO> queryArticlesByTag(Long tagId, PageParam page);

    /**
     * Match titles by keywords, query articles for recommendation, and only return articleId + title
     *
     * @param key
     * @return
     */
    List<SimpleArticleDTO> querySimpleArticleBySearchKey(String key);

    /**
     * Query article list based on search key, supporting pagination
     *
     * @param key
     * @param page
     * @return
     */
    PageListVo<ArticleDTO> queryArticlesBySearchKey(String key, PageParam page);

    /**
     * Query user's article list
     *
     * @param userId
     * @param pageParam
     * @param select
     * @return
     */
    PageListVo<ArticleDTO> queryArticlesByUserAndType(Long userId, PageParam pageParam, HomeSelectEnum select);

    /**
     * Supplement article entities with statistics, authors, category tags, etc.
     *
     * @param records
     * @param pageSize
     * @return
     */
    PageListVo<ArticleDTO> buildArticleListVo(List<ArticleDO> records, long pageSize);

    /**
     * Query hot articles
     *
     * @param pageParam
     * @return
     */
    PageListVo<SimpleArticleDTO> queryHotArticlesForRecommend(PageParam pageParam);

    /**
     * Query the number of articles by author
     *
     * @param authorId
     * @return
     */
    int queryArticleCount(long authorId);

    /**
     * Return the total article count
     *
     * @return
     */
    Long getArticleCount();
}

