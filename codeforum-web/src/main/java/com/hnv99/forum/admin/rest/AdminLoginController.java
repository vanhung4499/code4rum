package com.hnv99.forum.admin.rest;

import com.hnv99.forum.api.model.context.ReqInfoContext;
import com.hnv99.forum.api.model.vo.ResVo;
import com.hnv99.forum.api.model.vo.constants.StatusEnum;
import com.hnv99.forum.api.model.vo.user.dto.BaseUserInfoDTO;
import com.hnv99.forum.core.permission.Permission;
import com.hnv99.forum.core.permission.UserRole;
import com.hnv99.forum.core.util.SessionUtil;
import com.hnv99.forum.service.user.service.AuthorWhiteListService;
import com.hnv99.forum.service.user.service.LoginService;
import com.hnv99.forum.service.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * Article backend controller for managing login and logout.
 */
@RestController
@Api(value = "Backend Login and Logout Management Controller", tags = "Backend Login")
@RequestMapping(path = {"/api/admin", "/admin"})
public class AdminLoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private LoginService loginOutService;

    @Autowired
    private AuthorWhiteListService articleWhiteListService;

    /**
     * Login with username and password.
     *
     * @param request  The HTTP request.
     * @param response The HTTP response.
     * @return The response containing user information.
     */
    @RequestMapping(path = {"login"})
    public ResVo<BaseUserInfoDTO> login(HttpServletRequest request,
                                        HttpServletResponse response) {
        String username = request.getParameter("username");
        String pwd = request.getParameter("password");
        String session = loginOutService.loginByUserPwd(username, pwd);
        if (StringUtils.isNotBlank(session)) {
            // Write user login information to the cookie
            response.addCookie(SessionUtil.newCookie(LoginService.SESSION_KEY, session));
            return ResVo.ok(userService.queryBasicUserInfo(ReqInfoContext.getReqInfo().getUserId()));
        } else {
            return ResVo.fail(StatusEnum.LOGIN_FAILED_MIXED, "Login failed, please try again");
        }
    }

    /**
     * Check if the user is logged in.
     *
     * @return The response indicating whether the user is logged in.
     */
    @RequestMapping(path = "isLogined")
    public ResVo<Boolean> isLogined() {
        return ResVo.ok(ReqInfoContext.getReqInfo().getUserId() != null);
    }

    /**
     * Get information of the currently logged-in user.
     *
     * @return The response containing user information.
     */
    @ApiOperation("Get information of the currently logged-in user")
    @GetMapping("info")
    public ResVo<BaseUserInfoDTO> info() {
        BaseUserInfoDTO user = ReqInfoContext.getReqInfo().getUser();
        return ResVo.ok(user);
    }

    /**
     * Logout.
     *
     * @param response The HTTP response.
     * @return The response indicating successful logout.
     */
    @Permission(role = UserRole.LOGIN)
    @GetMapping("logout")
    public ResVo<Boolean> logOut(HttpServletResponse response) {
        Optional.ofNullable(ReqInfoContext.getReqInfo()).ifPresent(s -> loginOutService.logout(s.getSession()));
        // Why not implement redirection in the backend? Redirecting is done by the frontend to avoid problems caused by inconsistent port numbers during local development due to frontend and backend separation.
        // response.sendRedirect("/");

        // Remove cookie
        response.addCookie(SessionUtil.delCookie(LoginService.SESSION_KEY));
        return ResVo.ok(true);
    }
}

