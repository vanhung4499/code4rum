package com.hnv99.forum.service.config.repository.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hnv99.forum.api.model.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Entity class representing the global configuration table.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("global_conf")
public class GlobalConfigDO extends BaseDO {
    private static final long serialVersionUID = -6122208316544171301L;

    // Configuration item name
    @TableField("`key`")
    private String key;

    // Configuration item value
    private String value;

    // Remark
    private String comment;

    // Deleted flag
    private Integer deleted;
}
