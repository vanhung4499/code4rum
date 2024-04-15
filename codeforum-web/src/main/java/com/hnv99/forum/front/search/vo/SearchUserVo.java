package com.hnv99.forum.front.search.vo;

import com.hnv99.forum.api.model.vo.user.dto.SimpleUserInfoDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Represents user information for search.
 */
@Data
@ApiModel(value="User Information")
public class SearchUserVo implements Serializable {
    private static final long serialVersionUID = -2989169905031769195L;

    @ApiModelProperty("The keyword used for search")
    private String key;

    @ApiModelProperty("List of users")
    private List<SimpleUserInfoDTO> items;
}
