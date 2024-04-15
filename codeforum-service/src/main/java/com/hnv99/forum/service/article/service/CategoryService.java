package com.hnv99.forum.service.article.service;

import com.hnv99.forum.api.model.vo.article.dto.CategoryDTO;

import java.util.List;

/**
 * Category Service
 *
 * @author louzai
 * @date 2022-07-20
 */
public interface CategoryService {
    /**
     * Query category name by ID.
     *
     * @param categoryId Category ID
     * @return Category name
     */
    String queryCategoryName(Long categoryId);


    /**
     * Load all categories.
     *
     * @return List of CategoryDTO objects
     */
    List<CategoryDTO> loadAllCategories();

    /**
     * Query category ID by name.
     *
     * @param category Category name
     * @return Category ID
     */
    Long queryCategoryId(String category);


    /**
     * Refresh the cache.
     */
    void refreshCache();
}

