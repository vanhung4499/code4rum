package com.hnv99.forum.service.article.service;

import com.hnv99.forum.api.model.vo.PageVo;
import com.hnv99.forum.api.model.vo.article.*;
import com.hnv99.forum.api.model.vo.article.dto.ColumnArticleDTO;
import com.hnv99.forum.api.model.vo.article.dto.ColumnDTO;
import com.hnv99.forum.api.model.vo.article.dto.SimpleColumnDTO;

import java.util.List;

/**
 * Backend interface for managing columns
 */
public interface ColumnSettingService {

    /**
     * Save an article to the corresponding column
     *
     * @param articleId
     * @param columnId
     */
    void saveColumnArticle(Long articleId, Long columnId);

    /**
     * Save a column
     *
     * @param columnReq
     */
    void saveColumn(ColumnReq columnReq);

    /**
     * Save a column article
     *
     * @param req
     */
    void saveColumnArticle(ColumnArticleReq req);

    /**
     * Delete a column
     *
     * @param columnId
     */
    void deleteColumn(Long columnId);

    /**
     * Delete a column article
     *
     * @param id
     */
    void deleteColumnArticle(Long id);

    /**
     * Retrieve similar columns based on a keyword from their titles, returning only the primary key and title
     *
     * @param key
     * @return
     */
    List<SimpleColumnDTO> listSimpleColumnBySearchKey(String key);

    PageVo<ColumnDTO> getColumnList(SearchColumnReq req);

    PageVo<ColumnArticleDTO> getColumnArticleList(SearchColumnArticleReq req);

    void sortColumnArticleApi(SortColumnArticleReq req);

    void sortColumnArticleByIDApi(SortColumnArticleByIDReq req);
}
