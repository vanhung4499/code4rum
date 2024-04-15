package com.hnv99.forum.service.user.coverter;

import com.hnv99.forum.api.model.context.ReqInfoContext;
import com.hnv99.forum.api.model.enums.FollowStateEnum;
import com.hnv99.forum.api.model.enums.RoleEnum;
import com.hnv99.forum.api.model.vo.user.UserInfoSaveReq;
import com.hnv99.forum.api.model.vo.user.UserRelationReq;
import com.hnv99.forum.api.model.vo.user.UserSaveReq;
import com.hnv99.forum.api.model.vo.user.dto.BaseUserInfoDTO;
import com.hnv99.forum.api.model.vo.user.dto.SimpleUserInfoDTO;
import com.hnv99.forum.api.model.vo.user.dto.UserStatisticInfoDTO;
import com.hnv99.forum.service.user.repository.entity.UserDO;
import com.hnv99.forum.service.user.repository.entity.UserInfoDO;
import com.hnv99.forum.service.user.repository.entity.UserRelationDO;
import org.springframework.beans.BeanUtils;

/**
 * User Converter
 */
public class UserConverter {

    public static UserDO toDO(UserSaveReq req) {
        if (req == null) {
            return null;
        }
        UserDO userDO = new UserDO();
        userDO.setId(req.getUserId());
        userDO.setThirdAccountId(req.getThirdAccountId());
        userDO.setLoginType(req.getLoginType());
        return userDO;
    }

    public static UserInfoDO toDO(UserInfoSaveReq req) {
        if (req == null) {
            return null;
        }
        UserInfoDO userInfoDO = new UserInfoDO();
        userInfoDO.setUserId(req.getUserId());
        userInfoDO.setUserName(req.getUserName());
        userInfoDO.setPhoto(req.getPhoto());
        userInfoDO.setPosition(req.getPosition());
        userInfoDO.setCompany(req.getCompany());
        userInfoDO.setProfile(req.getProfile());
        return userInfoDO;
    }

    public static BaseUserInfoDTO toDTO(UserInfoDO info) {
        if (info == null) {
            return null;
        }
        BaseUserInfoDTO user = new BaseUserInfoDTO();
        // todo knowledge point, several ways to copy bean properties: direct get/set, BeanUtil utility class (spring, cglib, apache, objectMapper), serialization, etc.
        BeanUtils.copyProperties(info, user);
        // Set the user's latest login location
        user.setRegion(info.getIp().getLatestRegion());
        // Set user role
        user.setRole(RoleEnum.role(info.getUserRole()));
        return user;
    }

    public static SimpleUserInfoDTO toSimpleInfo(UserInfoDO info) {
        return new SimpleUserInfoDTO().setUserId(info.getUserId())
                .setName(info.getUserName())
                .setAvatar(info.getPhoto())
                .setProfile(info.getProfile());
    }

    public static UserRelationDO toDO(UserRelationReq req) {
        if (req == null) {
            return null;
        }
        UserRelationDO userRelationDO = new UserRelationDO();
        userRelationDO.setUserId(req.getUserId());
        userRelationDO.setFollowUserId(ReqInfoContext.getReqInfo().getUserId());
        userRelationDO.setFollowState(req.getFollowed() ? FollowStateEnum.FOLLOW.getCode() : FollowStateEnum.CANCEL_FOLLOW.getCode());
        return userRelationDO;
    }

    public static UserStatisticInfoDTO toUserHomeDTO(UserStatisticInfoDTO userHomeDTO, BaseUserInfoDTO baseUserInfoDTO) {
        if (baseUserInfoDTO == null) {
            return null;
        }
        BeanUtils.copyProperties(baseUserInfoDTO, userHomeDTO);
        return userHomeDTO;
    }
}

