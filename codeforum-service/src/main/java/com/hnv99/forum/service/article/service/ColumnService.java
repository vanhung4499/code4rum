package com.hnv99.forum.service.article.service;

import com.hnv99.forum.api.model.vo.PageListVo;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.article.dto.ColumnDTO;
import com.hnv99.forum.api.model.vo.article.dto.SimpleArticleDTO;
import com.hnv99.forum.service.article.repository.entity.ColumnArticleDO;

import java.util.List;

public interface ColumnService {
    /**
     * Build the corresponding column detail address based on the article ID
     *
     * @param articleId Article primary key
     * @return Column detail page
     */
    ColumnArticleDO getColumnArticleRelation(Long articleId);

    /**
     * Column list
     *
     * @param pageParam Page parameters
     * @return
     */
    PageListVo<ColumnDTO> listColumn(PageParam pageParam);

    /**
     * Get the Nth article in the column
     *
     * @param columnId Column ID
     * @param order     Order of the article
     * @return
     */
    ColumnArticleDO queryColumnArticle(long columnId, Integer order);

    /**
     * Only query basic column information, no need for statistics, authors, etc.
     *
     * @param columnId Column ID
     * @return
     */
    ColumnDTO queryBasicColumnInfo(Long columnId);

    /**
     * Column details
     *
     * @param columnId Column ID
     * @return
     */
    ColumnDTO queryColumnInfo(Long columnId);

    /**
     * Column + article list details
     *
     * @param columnId Column ID
     * @return
     */
    List<SimpleArticleDTO> queryColumnArticles(long columnId);

    /**
     * Returns the number of tutorials
     *
     * @return
     */
    Long getTutorialCount();
}
