package com.hnv99.forum.service.article.repository.params;

import com.hnv99.forum.api.model.vo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Article query parameters
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SearchArticleParams extends PageParam {

    /**
     * Article title
     */
    private String title;

    /**
     * Article ID
     */
    private Long articleId;

    /**
     * Author ID
     */
    private Long userId;

    /**
     * Author name
     */
    private String userName;

    /**
     * Article status: 0-Unpublished, 1-Published, 2-Under review
     */
    private Integer status;

    /**
     * Whether it is official: 0-Non-official, 1-Official
     */
    private Integer officialStat;

    /**
     * Whether it is pinned: 0-Not pinned, 1-Pinned
     */
    private Integer toppingStat;
}
