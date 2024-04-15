package com.hnv99.forum.service.user.service.user;

import com.hnv99.forum.api.model.context.ReqInfoContext;
import com.hnv99.forum.api.model.exception.ExceptionUtil;
import com.hnv99.forum.api.model.vo.constants.StatusEnum;
import com.hnv99.forum.api.model.vo.user.UserPwdLoginReq;
import com.hnv99.forum.api.model.vo.user.UserSaveReq;
import com.hnv99.forum.service.user.repository.dao.UserDao;
import com.hnv99.forum.service.user.repository.entity.UserDO;
import com.hnv99.forum.service.user.service.LoginService;
import com.hnv99.forum.service.user.service.RegisterService;
import com.hnv99.forum.service.user.service.UserService;
import com.hnv99.forum.service.user.service.help.UserPwdEncoder;
import com.hnv99.forum.service.user.service.help.UserSessionHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Login service based on captcha, username, and password.
 */
@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserSessionHelper userSessionHelper;

    @Autowired
    private RegisterService registerService;

    @Autowired
    private UserPwdEncoder userPwdEncoder;

    @Autowired
    private UserService userService;

    /**
     * When not registered, register a user first; if already registered, then login.
     *
     * @param req
     */
    private Long registerOrGetUserInfo(UserSaveReq req) {
        UserDO user = userDao.getByThirdAccountId(req.getThirdAccountId());
        if (user == null) {
            return registerService.registerByGoogle(req.getThirdAccountId());
        }
        return user.getId();
    }

    @Override
    public void logout(String session) {
        userSessionHelper.removeSession(session);
    }

    /**
     * Generate a session for login for users of WeChat public accounts.
     *
     * @param userId user ID
     * @return
     */
    @Override
    public String loginByWx(Long userId) {
        return userSessionHelper.genSession(userId);
    }

    /**
     * Login by username and password.
     *
     * @param username username
     * @param password password
     * @return
     */
    public String loginByUserPwd(String username, String password) {
        UserDO user = userDao.getUserByUserName(username);
        if (user == null) {
            throw ExceptionUtil.of(StatusEnum.USER_NOT_EXISTS, "userName=" + username);
        }

        if (!userPwdEncoder.match(password, user.getPassword())) {
            throw ExceptionUtil.of(StatusEnum.USER_PWD_ERROR);
        }

        Long userId = user.getId();

        // Successful login, return the corresponding session
        ReqInfoContext.getReqInfo().setUserId(userId);
        return userSessionHelper.genSession(userId);
    }


    /**
     * Login by username and password, if the user does not exist, then register.
     *
     * @param loginReq login information
     * @return
     */
    @Override
    public String registerByUserPwd(UserPwdLoginReq loginReq) {
        // Pre-check before registration
        registerPreCheck(loginReq);

        // Check if the current user is logged in, if logged in, then proceed to the binding process directly
        Long userId = ReqInfoContext.getReqInfo().getUserId();
        loginReq.setUserId(userId);
        if (userId != null) {
            // If the user is already logged in, proceed to the user information binding process
            userService.bindUserInfo(loginReq);
            return ReqInfoContext.getReqInfo().getSession();
        }


        // Try to log in using the username, if successful, proceed to the binding process
        UserDO user = userDao.getUserByUserName(loginReq.getUsername());
        if (user != null) {
            if (!userPwdEncoder.match(loginReq.getPassword(), user.getPassword())) {
                // Username already exists
                throw ExceptionUtil.of(StatusEnum.USER_LOGIN_NAME_REPEAT, loginReq.getUsername());
            }

            // User exists, try the binding process
            userId = user.getId();
            loginReq.setUserId(userId);
        } else {
            // Proceed to user registration process
            userId = registerService.registerByUserNameAndPassword(loginReq);
        }
        ReqInfoContext.getReqInfo().setUserId(userId);
        return userSessionHelper.genSession(userId);
    }


    /**
     * Pre-check before registration
     *
     * @param loginReq
     */
    private void registerPreCheck(UserPwdLoginReq loginReq) {
        if (StringUtils.isBlank(loginReq.getUsername()) || StringUtils.isBlank(loginReq.getPassword())) {
            throw ExceptionUtil.of(StatusEnum.USER_PWD_ERROR);
        }
    }
}
