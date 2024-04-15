package com.hnv99.forum.service.article.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hnv99.forum.api.model.entity.BaseDO;
import com.hnv99.forum.api.model.enums.PushStatusEnum;
import com.hnv99.forum.api.model.enums.SourceTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Article details
 * DO correspond to the database entity class
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("article_detail")
public class ArticleDetailDO extends BaseDO {

    private static final long serialVersionUID = 1L;

    /**
     * Article ID
     */
    private Long articleId;

    /**
     * Version number
     */
    private Long version;

    /**
     * Article content
     */
    private String content;

    private Integer deleted;
}
