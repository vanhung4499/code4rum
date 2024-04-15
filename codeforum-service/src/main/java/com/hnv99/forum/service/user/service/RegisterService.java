package com.hnv99.forum.service.user.service;

import com.hnv99.forum.api.model.vo.user.UserPwdLoginReq;

/**
 * Service interface for user registration.
 */
public interface RegisterService {
    /**
     * Register by username and password.
     *
     * @param loginReq The request containing username and password.
     * @return The user ID of the registered user.
     */
    Long registerByUserNameAndPassword(UserPwdLoginReq loginReq);

    /**
     * Register via WeChat Official Account.
     *
     * @param thirdAccount The third-party account associated with WeChat.
     * @return The user ID of the registered user.
     */
    Long registerByGoogle(String thirdAccount);
}
