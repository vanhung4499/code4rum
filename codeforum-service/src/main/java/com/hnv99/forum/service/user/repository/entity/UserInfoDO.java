package com.hnv99.forum.service.user.repository.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.hnv99.forum.api.model.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User personal information table
 */
@Data
@EqualsAndHashCode(callSuper = true)
// autoResultMap must exist, otherwise the IpInfo object cannot be correctly obtained
@TableName(value = "user_info", autoResultMap = true)
public class UserInfoDO extends BaseDO {

    private static final long serialVersionUID = 1L;

    /**
     * User ID
     */
    private Long userId;

    /**
     * Username
     */
    private String userName;

    /**
     * User avatar
     */
    private String photo;

    /**
     * Position
     */
    private String position;

    /**
     * Company
     */
    private String company;

    /**
     * Personal profile
     */
    private String profile;

    /**
     * Extended fields
     */
    private String extend;

    /**
     * Delete flag
     */
    private Integer deleted;

    /**
     * 0: ordinary user
     * 1: super administrator
     */
    private Integer userRole;

    /**
     * IP information
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private IpInfo ip;

    public IpInfo getIp() {
        if (ip == null) {
            ip = new IpInfo();
        }
        return ip;
    }
}

