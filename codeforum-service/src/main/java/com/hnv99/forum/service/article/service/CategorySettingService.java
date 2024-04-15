package com.hnv99.forum.service.article.service;

import com.hnv99.forum.api.model.vo.PageVo;
import com.hnv99.forum.api.model.vo.article.CategoryReq;
import com.hnv99.forum.api.model.vo.article.SearchCategoryReq;
import com.hnv99.forum.api.model.vo.article.dto.CategoryDTO;

/**
 * Backend interface for category management.
 *
 * @author louzai
 * @date 2022-09-17
 */
public interface CategorySettingService {

    /**
     * Save a category.
     *
     * @param categoryReq Category request object
     */
    void saveCategory(CategoryReq categoryReq);

    /**
     * Delete a category.
     *
     * @param categoryId ID of the category to delete
     */
    void deleteCategory(Integer categoryId);

    /**
     * Perform operations on a category.
     *
     * @param categoryId ID of the category
     * @param pushStatus Status of the push operation
     */
    void operateCategory(Integer categoryId, Integer pushStatus);

    /**
     * Get a list of categories.
     *
     * @param params Search parameters for category listing
     * @return PageVo containing a list of CategoryDTO objects
     */
    PageVo<CategoryDTO> getCategoryList(SearchCategoryReq params);
}

