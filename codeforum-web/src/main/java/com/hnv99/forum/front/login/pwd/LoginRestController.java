package com.hnv99.forum.front.login.pwd;

import com.hnv99.forum.api.model.context.ReqInfoContext;
import com.hnv99.forum.api.model.vo.ResVo;
import com.hnv99.forum.api.model.vo.constants.StatusEnum;
import com.hnv99.forum.api.model.vo.user.UserPwdLoginReq;
import com.hnv99.forum.core.permission.Permission;
import com.hnv99.forum.core.permission.UserRole;
import com.hnv99.forum.core.util.SessionUtil;
import com.hnv99.forum.service.user.service.LoginService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Endpoint for username/password-based login and logout.
 * Handles requests for logging in with username/password, registering a new account, and logging out.
 */
@RestController
public class LoginRestController {

    @Autowired
    private LoginService loginService;

    /**
     * Endpoint for username/password login.
     * Matches password based on username/planet number.
     *
     * @param username The username or planet number.
     * @param password The user's password.
     * @param response HttpServletResponse object for setting cookies.
     * @return A ResVo indicating if the login was successful.
     */
    @PostMapping("/login/username")
    public ResVo<Boolean> login(@RequestParam(name = "username") String username,
                                @RequestParam(name = "password") String password,
                                HttpServletResponse response) {
        String session = loginService.loginByUserPwd(username, password);
        if (StringUtils.isNotBlank(session)) {
            // Write user login information to cookie for identification
            response.addCookie(SessionUtil.newCookie(LoginService.SESSION_KEY, session));
            return ResVo.ok(true);
        } else {
            return ResVo.fail(StatusEnum.LOGIN_FAILED_MIXED, "An error occurred during username/password login. Please try again later.");
        }
    }

    /**
     * Endpoint for registering a new account.
     *
     * @param loginReq The UserPwdLoginReq object containing login details.
     * @param response HttpServletResponse object for setting cookies.
     * @return A ResVo indicating if the registration was successful.
     */
    @PostMapping("/login/register")
    public ResVo<Boolean> register(UserPwdLoginReq loginReq,
                                   HttpServletResponse response) {
        String session = loginService.registerByUserPwd(loginReq);
        if (StringUtils.isNotBlank(session)) {
            // Write user login information to cookie for identification
            response.addCookie(SessionUtil.newCookie(LoginService.SESSION_KEY, session));
            return ResVo.ok(true);
        } else {
            return ResVo.fail(StatusEnum.LOGIN_FAILED_MIXED, "An error occurred during username/password registration. Please try again later.");
        }
    }

    /**
     * Endpoint for logging out.
     * Invalidates the session and removes cookies.
     *
     * @param request HttpServletRequest object for session management.
     * @param response HttpServletResponse object for setting cookies and redirecting.
     * @return A ResVo indicating if the logout was successful.
     * @throws IOException If an I/O error occurs during redirection.
     */
    @Permission(role = UserRole.LOGIN)
    @RequestMapping("logout")
    public ResVo<Boolean> logOut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Invalidate session
        request.getSession().invalidate();
        Optional.ofNullable(ReqInfoContext.getReqInfo()).ifPresent(s -> loginService.logout(s.getSession()));
        // Remove cookie
        response.addCookie(SessionUtil.delCookie(LoginService.SESSION_KEY));
        // Redirect to the current page
        String referer = request.getHeader("Referer");
        if (StringUtils.isBlank(referer)) {
            referer = "/";
        }
        response.sendRedirect(referer);
        return ResVo.ok(true);
    }
}

