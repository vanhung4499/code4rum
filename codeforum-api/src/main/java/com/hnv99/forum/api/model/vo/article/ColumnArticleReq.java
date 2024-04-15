package com.hnv99.forum.api.model.vo.article;

import lombok.Data;

import java.io.Serializable;

/**
 * Save Column Article Request Parameters
 */
@Data
public class ColumnArticleReq implements Serializable {

    /**
     * Primary Key ID
     */
    private Long id;

    /**
     * Column ID
     */
    private Long columnId;

    /**
     * Article ID
     */
    private Long articleId;

    /**
     * Article Sorting
     */
    private Integer sort;

    /**
     * Tutorial Title
     */
    private String shortTitle;

    /**
     * Reading Method
     */
    private Integer read;
}
