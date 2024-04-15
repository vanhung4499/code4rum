package com.hnv99.forum.api.model.vo.article.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * DTO for column articles
 */
@Data
@Accessors(chain = true)
public class ColumnArticleDTO implements Serializable {
    private static final long serialVersionUID = 3646376715620165839L;

    /**
     * Unique ID
     */
    private Long id;

    /**
     * Article ID
     */
    private Long articleId;

    /**
     * Article title
     */
    private String title;

    /**
     * Short title of the article
     */
    private String shortTitle;

    /**
     * Column ID
     */
    private Long columnId;

    /**
     * Column title
     */
    private String column;

    /**
     * Column cover image
     */
    private String columnCover;

    /**
     * Article sorting order
     */
    private Integer sort;

    /**
     * Creation time
     */
    private Timestamp createTime;
}

