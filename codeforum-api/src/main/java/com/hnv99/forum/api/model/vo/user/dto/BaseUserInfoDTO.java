package com.hnv99.forum.api.model.vo.user.dto;

import com.hnv99.forum.api.model.entity.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Base user entity object.
 */
@Data
@ApiModel("Base user entity object")
@Accessors(chain = true)
public class BaseUserInfoDTO extends BaseDTO {
    /**
     * User ID
     */
    @ApiModelProperty(value = "User ID", required = true)
    private Long userId;

    /**
     * Username
     */
    @ApiModelProperty(value = "Username", required = true)
    private String userName;

    /**
     * User role: admin, normal
     */
    @ApiModelProperty(value = "Role", example = "ADMIN|NORMAL")
    private String role;

    /**
     * User photo
     */
    @ApiModelProperty(value = "User photo")
    private String photo;

    /**
     * Personal profile
     */
    @ApiModelProperty(value = "User profile")
    private String profile;

    /**
     * Position
     */
    @ApiModelProperty(value = "Personal position")
    private String position;

    /**
     * Company
     */
    @ApiModelProperty(value = "Company")
    private String company;

    /**
     * Additional fields
     */
    @ApiModelProperty(hidden = true)
    private String extend;

    /**
     * Deleted flag
     */
    @ApiModelProperty(hidden = true, value = "Whether the user is deleted")
    private Integer deleted;

    /**
     * User's last login region
     */
    @ApiModelProperty(value = "The geographic location of the user's last login", example = "HCMC,Vietnam")
    private String region;
}

