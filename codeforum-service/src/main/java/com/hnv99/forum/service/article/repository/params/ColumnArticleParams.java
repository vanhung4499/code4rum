package com.hnv99.forum.service.article.repository.params;

import lombok.Data;

/**
 * Parameters for column articles
 */
@Data
public class ColumnArticleParams {
    // Column ID
    private Long columnId;
    // Article ID
    private Long articleId;
    // Section
    private Integer section;
}

