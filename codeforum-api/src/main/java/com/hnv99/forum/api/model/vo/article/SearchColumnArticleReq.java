package com.hnv99.forum.api.model.vo.article;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("Tutorial Associated Article Query")
public class SearchColumnArticleReq {

    // Tutorial name
    @ApiModelProperty("Tutorial name")
    private String column;

    // Tutorial ID
    @ApiModelProperty("Tutorial ID")
    private Long columnId;

    // Article title
    @ApiModelProperty("Article title")
    private String articleTitle;

    @ApiModelProperty("Page number requested, counting from 1")
    private long pageNumber;

    @ApiModelProperty("Page size requested, default is 10")
    private long pageSize;
}

