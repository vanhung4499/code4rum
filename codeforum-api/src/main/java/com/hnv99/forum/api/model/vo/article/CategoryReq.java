package com.hnv99.forum.api.model.vo.article;

import lombok.Data;

import java.io.Serializable;

/**
 * Save Category Request Parameters
 */
@Data
public class CategoryReq implements Serializable {

    /**
     * ID
     */
    private Long categoryId;

    /**
     * Category Name
     */
    private String category;

    /**
     * Sorting
     */
    private Integer rank;
}

