package com.hnv99.forum.service.user.service.user;

import com.hnv99.forum.api.model.enums.NotifyTypeEnum;
import com.hnv99.forum.api.model.enums.user.LoginTypeEnum;
import com.hnv99.forum.api.model.exception.ExceptionUtil;
import com.hnv99.forum.api.model.vo.constants.StatusEnum;
import com.hnv99.forum.api.model.vo.notify.NotifyMsgEvent;
import com.hnv99.forum.api.model.vo.user.UserPwdLoginReq;
import com.hnv99.forum.core.util.SpringUtil;
import com.hnv99.forum.core.util.TransactionUtil;
import com.hnv99.forum.service.user.repository.dao.UserDao;
import com.hnv99.forum.service.user.repository.entity.UserDO;
import com.hnv99.forum.service.user.repository.entity.UserInfoDO;
import com.hnv99.forum.service.user.service.RegisterService;
import com.hnv99.forum.service.user.service.help.UserPwdEncoder;
import com.hnv99.forum.service.user.service.help.UserRandomGenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User Registration Service
 */
@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private UserPwdEncoder userPwdEncoder;
    @Autowired
    private UserDao userDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long registerByUserNameAndPassword(UserPwdLoginReq loginReq) {
        // 1. Check if the username is correct
        UserDO user = userDao.getUserByUserName(loginReq.getUsername());
        if (user != null) {
            throw ExceptionUtil.of(StatusEnum.USER_LOGIN_NAME_REPEAT, loginReq.getUsername());
        }

        // 2. Save user login information
        user = new UserDO();
        user.setUserName(loginReq.getUsername());
        user.setPassword(userPwdEncoder.encodePwd(loginReq.getPassword()));
        user.setThirdAccountId("");
        // Register with username and password
        user.setLoginType(LoginTypeEnum.USER_PWD.getType());
        userDao.saveUser(user);

        // 3. Save user information
        UserInfoDO userInfo = new UserInfoDO();
        userInfo.setUserId(user.getId());
        userInfo.setUserName(loginReq.getUsername());
        userInfo.setPhoto(UserRandomGenHelper.genAvatar());
        userDao.save(userInfo);

        processAfterUserRegister(user.getId());
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long registerByGoogle(String thirdAccount) {
        // User does not exist, registration is required
        // 1. Save user login information
        UserDO user = new UserDO();
        user.setThirdAccountId(thirdAccount);
        user.setLoginType(LoginTypeEnum.GOOGLE.getType());
        userDao.saveUser(user);

        // 2. Initialize user information, randomly generate user nickname + avatar
        UserInfoDO userInfo = new UserInfoDO();
        userInfo.setUserId(user.getId());
        userInfo.setUserName(UserRandomGenHelper.genNickName());
        userInfo.setPhoto(UserRandomGenHelper.genAvatar());
        userDao.save(userInfo);

        return user.getId();
    }


    /**
     * Actions triggered after user registration
     *
     * @param userId
     */
    private void processAfterUserRegister(Long userId) {
        TransactionUtil.registryAfterCommitOrImmediatelyRun(new Runnable() {
            @Override
            public void run() {
                // User registration event
                SpringUtil.publishEvent(new NotifyMsgEvent<>(this, NotifyTypeEnum.REGISTER, userId));
            }
        });
    }
}

