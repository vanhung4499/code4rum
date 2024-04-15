package com.hnv99.forum.api.model.vo.article.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Generic tag selection
 */
@Data
public class TagSelectDTO implements Serializable {

    /**
     * Type
     */
    private String selectType;

    /**
     * Description
     */
    private String selectDesc;

    /**
     * Whether selected
     */
    private Boolean selected;
}
