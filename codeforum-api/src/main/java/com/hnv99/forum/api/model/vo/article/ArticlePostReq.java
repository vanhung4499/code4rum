package com.hnv99.forum.api.model.vo.article;

import com.hnv99.forum.api.model.enums.ArticleTypeEnum;
import com.hnv99.forum.api.model.enums.PushStatusEnum;
import com.hnv99.forum.api.model.enums.SourceTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * Request parameters for publishing an article.
 */
@Data
public class ArticlePostReq implements Serializable {
    /**
     * Article ID. When present, it indicates updating an article.
     */
    private Long articleId;

    /**
     * Article title
     */
    private String title;

    /**
     * Article short title
     */
    private String shortTitle;

    /**
     * Category
     */
    private Long categoryId;

    /**
     * Tags
     */
    private Set<Long> tagIds;

    /**
     * Summary
     */
    private String summary;

    /**
     * Content
     */
    private String content;

    /**
     * Cover image
     */
    private String cover;

    /**
     * Article type
     *
     * @see ArticleTypeEnum
     */
    private String articleType;

    /**
     * Source: 1-Republished, 2-Original, 3-Translated
     *
     * @see SourceTypeEnum
     */
    private Integer source;

    /**
     * Status: 0-Unpublished, 1-Published
     *
     * @see PushStatusEnum
     */
    private Integer status;

    /**
     * Original article URL
     */
    private String sourceUrl;

    /**
     * POST - Publish, SAVE - Save draft, DELETE - Delete
     */
    private String actionType;

    /**
     * Column ID
     */
    private Long columnId;

    public PushStatusEnum pushStatus() {
        if ("post".equalsIgnoreCase(actionType)) {
            return PushStatusEnum.ONLINE;
        } else {
            return PushStatusEnum.OFFLINE;
        }
    }

    public boolean deleted() {
        return "delete".equalsIgnoreCase(actionType);
    }
}
