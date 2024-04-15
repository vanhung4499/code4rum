package com.hnv99.forum.service.user.service;

import com.hnv99.forum.api.model.vo.user.UserInfoSaveReq;
import com.hnv99.forum.api.model.vo.user.UserPwdLoginReq;
import com.hnv99.forum.api.model.vo.user.dto.BaseUserInfoDTO;
import com.hnv99.forum.api.model.vo.user.dto.SimpleUserInfoDTO;
import com.hnv99.forum.api.model.vo.user.dto.UserStatisticInfoDTO;
import com.hnv99.forum.service.user.repository.entity.UserDO;

import java.util.Collection;
import java.util.List;

/**
 * User Service Interface
 */
public interface UserService {
    /**
     * Check if the WeChat user has been registered
     *
     * @param wxuuid
     * @return
     */
    UserDO getWxUser(String wxuuid);

    /**
     * Search for users based on username
     *
     * @param userName username
     * @return
     */
    List<SimpleUserInfoDTO> searchUser(String userName);

    /**
     * Save user details
     *
     * @param req
     */
    void saveUserInfo(UserInfoSaveReq req);

    /**
     * Get and update user information based on session ID and client IP
     *
     * @param session  user session
     * @param clientIp user's latest login IP
     * @return return user basic information
     */
    BaseUserInfoDTO getAndUpdateUserIpInfoBySessionId(String session, String clientIp);

    /**
     * Query simple user information
     *
     * @param userId
     * @return
     */
    SimpleUserInfoDTO querySimpleUserInfo(Long userId);

    /**
     * Query basic user information
     * todo: caching optimization can be implemented
     *
     * @param userId
     * @return
     */
    BaseUserInfoDTO queryBasicUserInfo(Long userId);


    /**
     * Batch query simple user information
     *
     * @param userIds
     * @return
     */
    List<SimpleUserInfoDTO> batchQuerySimpleUserInfo(Collection<Long> userIds);

    /**
     * Batch query basic user information
     *
     * @param userIds
     * @return
     */
    List<BaseUserInfoDTO> batchQueryBasicUserInfo(Collection<Long> userIds);

    /**
     * Query user homepage information
     *
     * @param userId
     * @return
     * @throws Exception
     */
    UserStatisticInfoDTO queryUserInfoWithStatistic(Long userId);

    /**
     * Count users
     *
     * @return
     */
    Long getUserCount();

    /**
     * Bind user information
     */
    void bindUserInfo(UserPwdLoginReq loginReq);
}
