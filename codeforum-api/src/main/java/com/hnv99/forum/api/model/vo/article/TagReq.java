package com.hnv99.forum.api.model.vo.article;

import lombok.Data;

import java.io.Serializable;

/**
 * Save Tag Request Parameters
 */
@Data
public class TagReq implements Serializable {

    /**
     * ID
     */
    private Long tagId;

    /**
     * Tag Name
     */
    private String tag;

    /**
     * Category ID
     */
    private Long categoryId;
}

