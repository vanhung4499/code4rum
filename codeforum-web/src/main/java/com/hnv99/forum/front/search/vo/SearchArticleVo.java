package com.hnv99.forum.front.search.vo;

import com.hnv99.forum.api.model.vo.article.dto.SimpleArticleDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Represents search article information.
 */
@Data
@ApiModel(value="Article Information")
public class SearchArticleVo implements Serializable {
    private static final long serialVersionUID = -2989169905031769195L;

    @ApiModelProperty("The keyword used for search")
    private String key;

    @ApiModelProperty("List of articles")
    private List<SimpleArticleDTO> items;
}
