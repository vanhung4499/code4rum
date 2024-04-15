package com.hnv99.forum.service.article.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hnv99.forum.api.model.entity.BaseDO;
import com.hnv99.forum.api.model.enums.column.ColumnArticleReadEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Column article
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("column_article")
public class ColumnArticleDO extends BaseDO {
    private static final long serialVersionUID = -2372103913090667453L;

    private Long columnId;

    private Long articleId;

    /**
     * Order, smaller values come first
     */
    private Integer section;

    /**
     * Column type: free, login to read, paid read, etc.
     *
     * @see ColumnArticleReadEnum#getRead()
     */
    private Integer readType;
}
