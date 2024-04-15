package com.hnv99.forum.api.model.vo.article;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("Sorting column articles by ID and the new sorting value")
public class SortColumnArticleByIDReq implements Serializable {
    // ID of the article to be sorted
    @ApiModelProperty("ID of the article to be sorted")
    private Long id;
    // New sorting value
    @ApiModelProperty("New sorting value")
    private Integer sort;
}

