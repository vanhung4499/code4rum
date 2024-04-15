package com.hnv99.forum.service.article.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hnv99.forum.api.model.entity.BaseDO;
import com.hnv99.forum.api.model.enums.column.ColumnStatusEnum;
import com.hnv99.forum.api.model.enums.column.ColumnTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * Column information entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("column_info")
public class ColumnInfoDO extends BaseDO {

    private static final long serialVersionUID = 1920830534262012026L;

    /**
     * Column name
     */
    private String columnName;

    /**
     * Author
     */
    private Long userId;

    /**
     * Introduction
     */
    private String introduction;

    /**
     * Cover image
     */
    private String cover;

    /**
     * Status
     * @see ColumnStatusEnum#getCode()
     */
    private Integer state;

    /**
     * Order
     */
    private Integer section;

    /**
     * Online time
     */
    private Date publishTime;

    /**
     * Estimated number of articles in the column
     */
    private Integer nums;

    /**
     * Column type: free, login to read, paid read, etc.
     * @see ColumnTypeEnum#getType()
     */
    private Integer type;

    /**
     * Start time of free period
     */
    private Date freeStartTime;

    /**
     * End time of free period
     */
    private Date freeEndTime;
}
