package com.hnv99.forum.api.model.vo.article.dto;

import lombok.Data;
import lombok.ToString;

/**
 * DTO representing the creation history.
 */
@Data
@ToString(callSuper = true)
public class YearArticleDTO {

    /**
     * Year
     */
    private String year;

    /**
     * Number of articles
     */
    private Integer articleCount;
}
