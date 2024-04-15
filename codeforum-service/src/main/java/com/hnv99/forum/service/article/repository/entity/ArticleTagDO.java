package com.hnv99.forum.service.article.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hnv99.forum.api.model.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Mapping table for article tags
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("article_tag")
public class ArticleTagDO extends BaseDO {

    private static final long serialVersionUID = 1L;

    /**
     * Article ID
     */
    private Long articleId;

    /**
     * Tag ID
     */
    private Long tagId;

    private Integer deleted;
}

