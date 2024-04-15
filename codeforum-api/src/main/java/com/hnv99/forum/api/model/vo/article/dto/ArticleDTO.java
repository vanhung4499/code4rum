package com.hnv99.forum.api.model.vo.article.dto;

import com.hnv99.forum.api.model.enums.SourceTypeEnum;
import com.hnv99.forum.api.model.vo.user.dto.ArticleFootCountDTO;
import com.hnv99.forum.api.model.vo.user.dto.SimpleUserInfoDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Article information
 * <p>
 * DTO defines the entity class returned to the web frontend (VO)
 */
@Data
public class ArticleDTO implements Serializable {
    private static final long serialVersionUID = -793906904770296838L;

    private Long articleId;

    /**
     * Article type: 1-Blog, 2-Q&A
     */
    private Integer articleType;

    /**
     * Author UID
     */
    private Long author;

    /**
     * Author name
     */
    private String authorName;

    /**
     * Author avatar
     */
    private String authorAvatar;

    /**
     * Article title
     */
    private String title;

    /**
     * Short title
     */
    private String shortTitle;

    /**
     * Summary
     */
    private String summary;

    /**
     * Cover image
     */
    private String cover;

    /**
     * Main content
     */
    private String content;

    /**
     * Article source
     *
     * @see SourceTypeEnum
     */
    private String sourceType;

    /**
     * Original article URL
     */
    private String sourceUrl;

    /**
     * 0 Unpublished, 1 Published
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

    /**
     * Creation time
     */
    private Long createTime;

    /**
     * Last update time
     */
    private Long lastUpdateTime;

    /**
     * Category
     */
    private CategoryDTO category;

    /**
     * Tags
     */
    private List<TagDTO> tags;

    /**
     * Indicates whether the current viewer has already liked the article
     */
    private Boolean praised;

    /**
     * Indicates whether the current viewer has commented on the article
     */
    private Boolean commented;

    /**
     * Indicates whether the current viewer has bookmarked the article
     */
    private Boolean collected;

    /**
     * Statistics count for the article
     */
    private ArticleFootCountDTO count;

    /**
     * Information of users who liked the article
     */
    private List<SimpleUserInfoDTO> praisedUsers;
}

