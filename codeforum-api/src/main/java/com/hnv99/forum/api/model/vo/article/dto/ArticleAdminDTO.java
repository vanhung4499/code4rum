package com.hnv99.forum.api.model.vo.article.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Article information
 * <p>
 * DTO defines the entity class (VO) returned to the admin backend
 */
@Data
public class ArticleAdminDTO implements Serializable {
    private static final long serialVersionUID = -793906904770296838L;

    private Long articleId;

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
     * Cover image
     */
    private String cover;

    /**
     * 0: Unpublished, 1: Published
     */
    private Integer status;

    /**
     * Whether it is official
     */
    private Integer officialStat;

    /**
     * Whether it is pinned
     */
    private Integer toppingStat;

    /**
     * Whether it is featured
     */
    private Integer creamStat;

    // Update time
    private Date updateTime;

}

