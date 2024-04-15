package com.hnv99.forum.service.article.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hnv99.forum.api.model.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * fixme Access count, to be replaced by Redis later
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("read_count")
public class ReadCountDO extends BaseDO {
    /**
     * Document ID (article/comment)
     */
    private Long documentId;

    /**
     * Document type: 1 - article, 2 - comment
     */
    private Integer documentType;

    /**
     * Count
     */
    private Integer cnt;

}
