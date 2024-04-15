package com.hnv99.forum.api.model.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class BaseDTO {
    @ApiModelProperty(value = "business primary key")
    private Long id;

    @ApiModelProperty(value = "Create time")
    private Date createTime;

    @ApiModelProperty(value = "Last modify time")
    private Date updateTime;
}
