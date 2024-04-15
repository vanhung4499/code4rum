package com.hnv99.forum.service.user.service;

import com.hnv99.forum.api.model.vo.PageListVo;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.user.UserRelationReq;
import com.hnv99.forum.api.model.vo.user.dto.FollowUserInfoDTO;

import java.util.List;
import java.util.Set;

/**
 * User Relationship Service Interface
 */
public interface UserRelationService {

    /**
     * Get the users that I follow
     *
     * @param userId
     * @param pageParam
     * @return
     */
    PageListVo<FollowUserInfoDTO> getUserFollowList(Long userId, PageParam pageParam);


    /**
     * Get the fans who follow me
     *
     * @param userId
     * @param pageParam
     * @return
     */
    PageListVo<FollowUserInfoDTO> getUserFansList(Long userId, PageParam pageParam);

    /**
     * Update the follow relationship between the currently logged-in user and the users in the list
     *
     * @param followList
     * @param loginUserId
     */
    void updateUserFollowRelationId(PageListVo<FollowUserInfoDTO> followList, Long loginUserId);

    /**
     * Find the IDs of users already followed by the logged-in user from the given user list
     *
     * @param userIds
     * @param loginUserId
     * @return
     */
    Set<Long> getFollowedUserId(List<Long> userIds, Long loginUserId);

    /**
     * Save user relationship: follow or unfollow
     *
     * @param req
     * @throws Exception
     */
    void saveUserRelation(UserRelationReq req);
}
