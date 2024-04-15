package com.hnv99.forum.api.model.vo.article;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("Sorting column articles")
public class SortColumnArticleReq implements Serializable {
    // ID of the article before sorting
    @ApiModelProperty("ID of the article before sorting")
    private Long activeId;

    // ID of the article after sorting
    @ApiModelProperty("ID of the article after sorting")
    private Long overId;
}

