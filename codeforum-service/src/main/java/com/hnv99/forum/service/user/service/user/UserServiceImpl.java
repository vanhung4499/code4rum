package com.hnv99.forum.service.user.service.user;

import com.hnv99.forum.api.model.context.ReqInfoContext;
import com.hnv99.forum.api.model.exception.ExceptionUtil;
import com.hnv99.forum.api.model.vo.article.dto.YearArticleDTO;
import com.hnv99.forum.api.model.vo.constants.StatusEnum;
import com.hnv99.forum.api.model.vo.user.UserInfoSaveReq;
import com.hnv99.forum.api.model.vo.user.UserPwdLoginReq;
import com.hnv99.forum.api.model.vo.user.dto.BaseUserInfoDTO;
import com.hnv99.forum.api.model.vo.user.dto.SimpleUserInfoDTO;
import com.hnv99.forum.api.model.vo.user.dto.UserStatisticInfoDTO;
import com.hnv99.forum.core.util.IpUtil;
import com.hnv99.forum.service.article.repository.dao.ArticleDao;
import com.hnv99.forum.service.statistics.service.CountService;
import com.hnv99.forum.service.user.coverter.UserConverter;
import com.hnv99.forum.service.user.repository.dao.UserDao;
import com.hnv99.forum.service.user.repository.dao.UserRelationDao;
import com.hnv99.forum.service.user.repository.entity.IpInfo;
import com.hnv99.forum.service.user.repository.entity.UserDO;
import com.hnv99.forum.service.user.repository.entity.UserInfoDO;
import com.hnv99.forum.service.user.repository.entity.UserRelationDO;
import com.hnv99.forum.service.user.service.UserService;
import com.hnv99.forum.service.user.service.help.UserPwdEncoder;
import com.hnv99.forum.service.user.service.help.UserSessionHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * User Service
 *
 * @author louzai
 * @date 2022-07-20
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    @Resource
    private UserRelationDao userRelationDao;

    @Autowired
    private CountService countService;

    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private UserSessionHelper userSessionHelper;

    @Autowired
    private UserPwdEncoder userPwdEncoder;

    @Override
    public UserDO getWxUser(String wxuuid) {
        return userDao.getByThirdAccountId(wxuuid);
    }

    @Override
    public List<SimpleUserInfoDTO> searchUser(String userName) {
        List<UserInfoDO> users = userDao.getByUserNameLike(userName);
        if (CollectionUtils.isEmpty(users)) {
            return Collections.emptyList();
        }
        return users.stream().map(s -> new SimpleUserInfoDTO()
                        .setUserId(s.getUserId())
                        .setName(s.getUserName())
                        .setAvatar(s.getPhoto())
                        .setProfile(s.getProfile())
                )
                .collect(Collectors.toList());
    }

    @Override
    public void saveUserInfo(UserInfoSaveReq req) {
        UserInfoDO userInfoDO = UserConverter.toDO(req);
        userDao.updateUserInfo(userInfoDO);
    }

    @Override
    public BaseUserInfoDTO getAndUpdateUserIpInfoBySessionId(String session, String clientIp) {
        if (StringUtils.isBlank(session)) {
            return null;
        }

        Long userId = userSessionHelper.getUserIdBySession(session);
        if (userId == null) {
            return null;
        }

        // Query user information and update the last used IP
        UserInfoDO user = userDao.getByUserId(userId);
        if (user == null) {
            throw ExceptionUtil.of(StatusEnum.USER_NOT_EXISTS, "userId=" + userId);
        }

        IpInfo ip = user.getIp();
        if (clientIp != null && !Objects.equals(ip.getLatestIp(), clientIp)) {
            // Different IP, need to update
            ip.setLatestIp(clientIp);
            ip.setLatestRegion(IpUtil.getLocationByIp(clientIp).toRegionStr());

            if (ip.getFirstIp() == null) {
                ip.setFirstIp(clientIp);
                ip.setFirstRegion(ip.getLatestRegion());
            }
            userDao.updateById(user);
        }

        return UserConverter.toDTO(user);
    }

    public SimpleUserInfoDTO querySimpleUserInfo(Long userId) {
        UserInfoDO user = userDao.getByUserId(userId);
        if (user == null) {
            throw ExceptionUtil.of(StatusEnum.USER_NOT_EXISTS, "userId=" + userId);
        }
        return UserConverter.toSimpleInfo(user);
    }

    @Override
    public BaseUserInfoDTO queryBasicUserInfo(Long userId) {
        UserInfoDO user = userDao.getByUserId(userId);
        if (user == null) {
            throw ExceptionUtil.of(StatusEnum.USER_NOT_EXISTS, "userId=" + userId);
        }
        return UserConverter.toDTO(user);
    }

    public List<SimpleUserInfoDTO> batchQuerySimpleUserInfo(Collection<Long> userIds) {
        List<UserInfoDO> users = userDao.getByUserIds(userIds);
        if (CollectionUtils.isEmpty(users)) {
            throw ExceptionUtil.of(StatusEnum.USER_NOT_EXISTS, "userId=" + userIds);
        }
        return users.stream().map(UserConverter::toSimpleInfo).collect(Collectors.toList());
    }

    public List<BaseUserInfoDTO> batchQueryBasicUserInfo(Collection<Long> userIds) {
        List<UserInfoDO> users = userDao.getByUserIds(userIds);
        if (CollectionUtils.isEmpty(users)) {
            throw ExceptionUtil.of(StatusEnum.USER_NOT_EXISTS, "userId=" + userIds);
        }
        return users.stream().map(UserConverter::toDTO).collect(Collectors.toList());
    }

    @Override
    public UserStatisticInfoDTO queryUserInfoWithStatistic(Long userId) {
        BaseUserInfoDTO userInfoDTO = queryBasicUserInfo(userId);
        UserStatisticInfoDTO userHomeDTO = countService.queryUserStatisticInfo(userId);
        userHomeDTO = UserConverter.toUserHomeDTO(userHomeDTO, userInfoDTO);

        // User profile completeness
        int cnt = 0;
        if (StringUtils.isNotBlank(userHomeDTO.getCompany())) {
            ++cnt;
        }
        if (StringUtils.isNotBlank(userHomeDTO.getPosition())) {
            ++cnt;
        }
        if (StringUtils.isNotBlank(userHomeDTO.getProfile())) {
            ++cnt;
        }
        userHomeDTO.setInfoPercent(cnt * 100 / 3);

        // Whether followed
        Long followUserId = ReqInfoContext.getReqInfo().getUserId();
        if (followUserId != null) {
            UserRelationDO userRelationDO = userRelationDao.getUserRelationByUserId(userId, followUserId);
            userHomeDTO.setFollowed((userRelationDO == null) ? Boolean.FALSE : Boolean.TRUE);
        } else {
            userHomeDTO.setFollowed(Boolean.FALSE);
        }

        // Days since joining
        int joinDayCount = (int) ((System.currentTimeMillis() - userHomeDTO.getCreateTime()
                .getTime()) / (1000 * 3600 * 24));
        userHomeDTO.setJoinDayCount(Math.max(1, joinDayCount));

        // Creation history
        List<YearArticleDTO> yearArticleDTOS = articleDao.listYearArticleByUserId(userId);
        userHomeDTO.setYearArticleList(yearArticleDTOS);
        return userHomeDTO;
    }

    @Override
    public Long getUserCount() {
        return this.userDao.getUserCount();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindUserInfo(UserPwdLoginReq loginReq) {
        // 0. Pre-validation before binding username & password
        UserDO user = userDao.getUserByUserName(loginReq.getUsername());
        if (user == null) {
            // If the username does not exist, it means the current login user can use this username
            user = new UserDO();
            user.setId(loginReq.getUserId());
        } else if (!Objects.equals(loginReq.getUserId(), user.getId())) {
            // The login username already exists
            throw ExceptionUtil.of(StatusEnum.USER_LOGIN_NAME_REPEAT, loginReq.getUsername());
        }

        // 1. Update username and password
        user.setUserName(loginReq.getUsername());
        user.setPassword(userPwdEncoder.encodePwd(loginReq.getPassword()));
        userDao.saveUser(user);
    }
}

