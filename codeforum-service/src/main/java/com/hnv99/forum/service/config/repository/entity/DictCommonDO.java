package com.hnv99.forum.service.config.repository.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hnv99.forum.api.model.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Entity class representing the common data dictionary.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("dict_common")
public class DictCommonDO extends BaseDO {
    /**
     * Dictionary type code.
     */
    @TableField("type_code")
    private String typeCode;

    /**
     * Value code of the dictionary type.
     */
    @TableField("dict_code")
    private String dictCode;

    /**
     * Description of the value of the dictionary type.
     */
    @TableField("dict_desc")
    private String dictDesc;

    /**
     * Sorting number.
     */
    @TableField("sort_no")
    private Integer sortNo;

    /**
     * Remark.
     */
    @TableField("remark")
    private String remark;
}

