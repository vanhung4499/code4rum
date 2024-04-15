package com.hnv99.forum.service.article.repository.params;

import com.hnv99.forum.api.model.vo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Column article query
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SearchColumnArticleParams extends PageParam {

    /**
     * Column name
     */
    private String column;

    /**
     * Column ID
     */
    private Long columnId;

    /**
     * Article title
     */
    private String articleTitle;
}

