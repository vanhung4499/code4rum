package com.hnv99.forum.front.search.vo;

import com.hnv99.forum.api.model.vo.article.dto.SimpleColumnDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Represents search column information.
 */
@Data
@ApiModel(value="Column Information")
public class SearchColumnVo implements Serializable {
    private static final long serialVersionUID = -2989169905031769195L;

    @ApiModelProperty("The keyword used for search")
    private String key;

    @ApiModelProperty("List of columns")
    private List<SimpleColumnDTO> items;
}
