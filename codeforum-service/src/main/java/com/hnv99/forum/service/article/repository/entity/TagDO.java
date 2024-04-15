package com.hnv99.forum.service.article.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hnv99.forum.api.model.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Tag management table
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tag")
public class TagDO extends BaseDO {
    private static final long serialVersionUID = 3796460143933607644L;

    /**
     * Tag name
     */
    private String tagName;

    /**
     * Tag type: 1 - system tag, 2 - custom tag
     */
    private Integer tagType;

    /**
     * Status: 0 - unpublished, 1 - published
     */
    private Integer status;

    /**
     * Whether deleted
     */
    private Integer deleted;
}
