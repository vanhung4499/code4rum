package com.hnv99.forum.api.model.vo.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Basic user information.
 */
@Data
@Accessors(chain = true)
public class SimpleUserInfoDTO implements Serializable {
    private static final long serialVersionUID = 4802653694786272120L;

    @ApiModelProperty("Author ID")
    private Long userId;

    @ApiModelProperty("Author name")
    private String name;

    @ApiModelProperty("Author avatar")
    private String avatar;

    @ApiModelProperty("Author profile")
    private String profile;
}
