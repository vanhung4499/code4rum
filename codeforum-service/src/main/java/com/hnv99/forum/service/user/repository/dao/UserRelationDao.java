package com.hnv99.forum.service.user.repository.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnv99.forum.api.model.enums.FollowStateEnum;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.user.dto.FollowUserInfoDTO;
import com.hnv99.forum.service.user.repository.entity.UserRelationDO;
import com.hnv99.forum.service.user.repository.mapper.UserRelationMapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * User-related DB operations
 */
@Repository
public class UserRelationDao extends ServiceImpl<UserRelationMapper, UserRelationDO> {

    /**
     * Query the user's follow list
     *
     * @param followUserId
     * @param pageParam
     * @return
     */
    public List<FollowUserInfoDTO> listUserFollows(Long followUserId, PageParam pageParam) {
        return baseMapper.queryUserFollowList(followUserId, pageParam);
    }

    /**
     * Query the user's fans list, i.e., users who follow userId
     *
     * @param userId
     * @param pageParam
     * @return
     */
    public List<FollowUserInfoDTO> listUserFans(Long userId, PageParam pageParam) {
        return baseMapper.queryUserFansList(userId, pageParam);
    }

    /**
     * Query the association between followUserId and the given user list
     *
     * @param followUserId fan user id
     * @param targetUserId list of follower user ids
     * @return
     */
    public List<UserRelationDO> listUserRelations(Long followUserId, Collection<Long> targetUserId) {
        return lambdaQuery().eq(UserRelationDO::getFollowUserId, followUserId)
                .in(UserRelationDO::getUserId, targetUserId).list();
    }

    public Long queryUserFollowCount(Long userId) {
        QueryWrapper<UserRelationDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(UserRelationDO::getFollowUserId, userId)
                .eq(UserRelationDO::getFollowState, FollowStateEnum.FOLLOW.getCode());
        return baseMapper.selectCount(queryWrapper);
    }

    public Long queryUserFansCount(Long userId) {
        QueryWrapper<UserRelationDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(UserRelationDO::getUserId, userId)
                .eq(UserRelationDO::getFollowState, FollowStateEnum.FOLLOW.getCode());
        return baseMapper.selectCount(queryWrapper);
    }

    /**
     * Get follow information
     *
     * @param userId       login user
     * @param followUserId followed user
     * @return
     */
    public UserRelationDO getUserRelationByUserId(Long userId, Long followUserId) {
        QueryWrapper<UserRelationDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(UserRelationDO::getUserId, userId)
                .eq(UserRelationDO::getFollowUserId, followUserId)
                .eq(UserRelationDO::getFollowState, FollowStateEnum.FOLLOW.getCode());
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * Get follow record
     *
     * @param userId       login user
     * @param followUserId followed user
     * @return
     */
    public UserRelationDO getUserRelationRecord(Long userId, Long followUserId) {
        QueryWrapper<UserRelationDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(UserRelationDO::getUserId, userId)
                .eq(UserRelationDO::getFollowUserId, followUserId);
        return baseMapper.selectOne(queryWrapper);
    }
}

