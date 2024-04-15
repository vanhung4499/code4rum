package com.hnv99.forum.api.model.vo.article.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class SimpleColumnDTO implements Serializable {

    private static final long serialVersionUID = 3646376715620165839L;

    @ApiModelProperty("Column ID")
    private Long columnId;

    @ApiModelProperty("Column name")
    private String column;

    // Cover
    @ApiModelProperty("Cover")
    private String cover;
}
