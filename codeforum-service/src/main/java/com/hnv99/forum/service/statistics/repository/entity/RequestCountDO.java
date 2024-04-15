package com.hnv99.forum.service.statistics.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hnv99.forum.api.model.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * Request Count Table
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("request_count")
public class RequestCountDO extends BaseDO {

    private static final long serialVersionUID = 1L;

    /**
     * Machine IP
     */
    private String host;

    /**
     * Access count
     */
    private Integer cnt;

    /**
     * Current date
     */
    private Date date;
}

