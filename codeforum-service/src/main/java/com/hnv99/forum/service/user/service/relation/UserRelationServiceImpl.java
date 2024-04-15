package com.hnv99.forum.service.user.service.relation;

import com.hnv99.forum.api.model.context.ReqInfoContext;
import com.hnv99.forum.api.model.enums.FollowStateEnum;
import com.hnv99.forum.api.model.enums.NotifyTypeEnum;
import com.hnv99.forum.api.model.vo.PageListVo;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.notify.NotifyMsgEvent;
import com.hnv99.forum.api.model.vo.user.UserRelationReq;
import com.hnv99.forum.api.model.vo.user.dto.FollowUserInfoDTO;
import com.hnv99.forum.core.util.MapUtils;
import com.hnv99.forum.core.util.SpringUtil;
import com.hnv99.forum.service.user.coverter.UserConverter;
import com.hnv99.forum.service.user.repository.dao.UserRelationDao;
import com.hnv99.forum.service.user.repository.entity.UserRelationDO;
import com.hnv99.forum.service.user.service.UserRelationService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User Relationship Service
 *
 * Manages user relationships such as followers and fans.
 */
@Service
public class UserRelationServiceImpl implements UserRelationService {
    @Resource
    private UserRelationDao userRelationDao;

    /**
     * Retrieve a user's list of followed users.
     *
     * @param userId    The user ID
     * @param pageParam Pagination parameters
     * @return A paginated list of followed users
     */
    @Override
    public PageListVo<FollowUserInfoDTO> getUserFollowList(Long userId, PageParam pageParam) {
        List<FollowUserInfoDTO> userRelationList = userRelationDao.listUserFollows(userId, pageParam);
        return PageListVo.newVo(userRelationList, pageParam.getPageSize());
    }

    /**
     * Retrieve a user's list of fans.
     *
     * @param userId    The user ID
     * @param pageParam Pagination parameters
     * @return A paginated list of fans
     */
    @Override
    public PageListVo<FollowUserInfoDTO> getUserFansList(Long userId, PageParam pageParam) {
        List<FollowUserInfoDTO> userRelationList = userRelationDao.listUserFans(userId, pageParam);
        return PageListVo.newVo(userRelationList, pageParam.getPageSize());
    }

    /**
     * Update user follow relation ID.
     *
     * @param followList   List of users to update
     * @param loginUserId  ID of the logged-in user
     */
    @Override
    public void updateUserFollowRelationId(PageListVo<FollowUserInfoDTO> followList, Long loginUserId) {
        // If the login user ID is null, set all relation IDs to null and followed status to false
        if (loginUserId == null) {
            followList.getList().forEach(r -> {
                r.setRelationId(null);
                r.setFollowed(false);
            });
            return;
        }

        // Check the follow relationship between the logged-in user and the given user list
        Set<Long> userIds = followList.getList().stream().map(FollowUserInfoDTO::getUserId).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }

        List<UserRelationDO> relationList = userRelationDao.listUserRelations(loginUserId, userIds);
        Map<Long, UserRelationDO> relationMap = MapUtils.toMap(relationList, UserRelationDO::getUserId, r -> r);
        followList.getList().forEach(follow -> {
            UserRelationDO relation = relationMap.get(follow.getUserId());
            if (relation == null) {
                follow.setRelationId(null);
                follow.setFollowed(false);
            } else if (Objects.equals(relation.getFollowState(), FollowStateEnum.FOLLOW.getCode())) {
                follow.setRelationId(relation.getId());
                follow.setFollowed(true);
            } else {
                follow.setRelationId(relation.getId());
                follow.setFollowed(false);
            }
        });
    }

    /**
     * Get followed user IDs from the given user list by the login user.
     *
     * @param userIds    List of primary users
     * @param fansUserId ID of the fan user
     * @return A set of user IDs that the fansUserId has followed
     */
    public Set<Long> getFollowedUserId(List<Long> userIds, Long fansUserId) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptySet();
        }

        List<UserRelationDO> relationList = userRelationDao.listUserRelations(fansUserId, userIds);
        Map<Long, UserRelationDO> relationMap = MapUtils.toMap(relationList, UserRelationDO::getUserId, r -> r);
        return relationMap.values().stream().filter(s -> s.getFollowState().equals(FollowStateEnum.FOLLOW.getCode())).map(UserRelationDO::getUserId).collect(Collectors.toSet());
    }

    /**
     * Save user relation.
     *
     * @param req User relation request
     */
    @Override
    public void saveUserRelation(UserRelationReq req) {
        // Check if the record exists
        UserRelationDO userRelationDO = userRelationDao.getUserRelationRecord(req.getUserId(), ReqInfoContext.getReqInfo().getUserId());
        if (userRelationDO == null) {
            userRelationDO = UserConverter.toDO(req);
            userRelationDao.save(userRelationDO);
            // Publish follow event
            SpringUtil.publishEvent(new NotifyMsgEvent<>(this, NotifyTypeEnum.FOLLOW, userRelationDO));
            return;
        }

        // Reset the follow state
        userRelationDO.setFollowState(req.getFollowed() ? FollowStateEnum.FOLLOW.getCode() : FollowStateEnum.CANCEL_FOLLOW.getCode());
        userRelationDao.updateById(userRelationDO);
        // Publish follow or cancel follow event
        SpringUtil.publishEvent(new NotifyMsgEvent<>(this, req.getFollowed() ? NotifyTypeEnum.FOLLOW : NotifyTypeEnum.CANCEL_FOLLOW, userRelationDO));
    }
}

