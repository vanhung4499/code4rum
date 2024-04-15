package com.hnv99.forum.service.user.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.user.dto.FollowUserInfoDTO;
import com.hnv99.forum.service.user.repository.entity.UserRelationDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * User relationship mapper interface
 */
public interface UserRelationMapper extends BaseMapper<UserRelationDO> {

    /**
     * Users I follow
     * @param followUserId
     * @param pageParam
     * @return
     */
    List<FollowUserInfoDTO> queryUserFollowList(@Param("followUserId") Long followUserId, @Param("pageParam") PageParam pageParam);

    /**
     * Fans who follow me
     * @param userId
     * @param pageParam
     * @return
     */
    List<FollowUserInfoDTO> queryUserFansList(@Param("userId") Long userId, @Param("pageParam") PageParam pageParam);
}
