package com.hnv99.forum.service.article.repository.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hnv99.forum.api.model.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Category management table
 *
 * @author louzai
 * @date 2022-07-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("category")
public class CategoryDO extends BaseDO {

    private static final long serialVersionUID = 1L;

    /**
     * Category name
     */
    private String categoryName;

    /**
     * Status: 0 - unpublished, 1 - published
     */
    private Integer status;

    /**
     * Sorting
     */
    @TableField("`rank`")
    private Integer rank;

    private Integer deleted;
}
