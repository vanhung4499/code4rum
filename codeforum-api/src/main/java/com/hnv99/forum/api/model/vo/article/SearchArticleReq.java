package com.hnv99.forum.api.model.vo.article;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("Article Query")
public class SearchArticleReq {

    // Article title
    @ApiModelProperty("Article title")
    private String title;

    @ApiModelProperty("Article ID")
    private Long articleId;

    @ApiModelProperty("Author ID")
    private Long userId;

    @ApiModelProperty("Author name")
    private String userName;

    @ApiModelProperty("Article status: 0-Unpublished, 1-Published, 2-Under review")
    private Integer status;

    @ApiModelProperty("Official status: 0-Non-official, 1-Official")
    private Integer officalStat;

    @ApiModelProperty("Top status: 0-Not top, 1-Top")
    private Integer toppingStat;

    @ApiModelProperty("Page number requested, counting from 1")
    private long pageNumber;

    @ApiModelProperty("Page size requested, default is 10")
    private long pageSize;
}
