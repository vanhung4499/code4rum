package com.hnv99.forum.service.article.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hnv99.forum.api.model.entity.BaseDO;
import com.hnv99.forum.api.model.enums.PushStatusEnum;
import com.hnv99.forum.api.model.enums.SourceTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Article table
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("article")
public class ArticleDO extends BaseDO {
    private static final long serialVersionUID = 1L;

    /**
     * Author
     */
    private Long userId;

    /**
     * Article type: 1 - blog post, 2 - Q&A, 3 - column article
     */
    private Integer articleType;

    /**
     * Article title
     */
    private String title;

    /**
     * Short title
     */
    private String shortTitle;

    /**
     * Article cover picture
     */
    private String picture;

    /**
     * Article summary
     */
    private String summary;

    /**
     * Category ID
     */
    private Long categoryId;

    /**
     * Source: 1 - reprint, 2 - original, 3 - translation
     *
     * @see SourceTypeEnum
     */
    private Integer source;

    /**
     * Original article URL
     */
    private String sourceUrl;

    /**
     * Status: 0 - unpublished, 1 - published
     *
     * @see PushStatusEnum
     */
    private Integer status;

    /**
     * Whether official
     */
    private Integer officialStat;

    /**
     * Whether pinned
     */
    private Integer toppingStat;

    /**
     * Whether featured
     */
    private Integer creamStat;

    private Integer deleted;
}
