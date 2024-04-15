package com.hnv99.forum.service.user.service;

import com.hnv99.forum.api.model.vo.user.UserPwdLoginReq;

/**
 * Interface for user login service.
 */
public interface LoginService {
    String SESSION_KEY = "f-session";
    String USER_DEVICE_KEY = "f-device";

    /**
     * Log out.
     *
     * @param session user session
     */
    void logout(String session);

    /**
     * Generate a session for users of WeChat official account for login.
     *
     * @param userId user primary key id
     * @return session
     */
    String loginByWx(Long userId);

    /**
     * User login using username and password.
     *
     * @param username username
     * @param password password
     * @return session
     */
    String loginByUserPwd(String username, String password);

    /**
     * Register and log in, and bind corresponding planet and invitation code.
     *
     * @param loginReq login information
     * @return session
     */
    String registerByUserPwd(UserPwdLoginReq loginReq);
}
