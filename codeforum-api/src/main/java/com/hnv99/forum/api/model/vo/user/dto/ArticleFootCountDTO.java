package com.hnv99.forum.api.model.vo.user.dto;

import lombok.Data;

/**
 * DTO representing statistics for an article's footprint.
 */
@Data
public class ArticleFootCountDTO {

    /**
     * Number of likes for the article.
     */
    private Integer praiseCount;

    /**
     * Number of reads for the article.
     */
    private Integer readCount;

    /**
     * Number of times the article has been bookmarked.
     */
    private Integer collectionCount;

    /**
     * Number of comments on the article.
     */
    private Integer commentCount;

    /**
     * Default constructor initializing all counts to 0.
     */
    public ArticleFootCountDTO() {
        praiseCount = 0;
        readCount = 0;
        collectionCount = 0;
        commentCount = 0;
    }
}
