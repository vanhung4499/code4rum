package com.hnv99.forum.api.model.vo.article;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("Tutorial Query")
public class SearchColumnReq {

    // Tutorial name
    @ApiModelProperty("Tutorial name")
    private String column;

    @ApiModelProperty("Page number requested, counting from 1")
    private long pageNumber;

    @ApiModelProperty("Page size requested, default is 10")
    private long pageSize;
}
