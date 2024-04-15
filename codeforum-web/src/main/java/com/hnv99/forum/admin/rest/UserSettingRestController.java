package com.hnv99.forum.admin.rest;

import com.hnv99.forum.api.model.context.ReqInfoContext;
import com.hnv99.forum.api.model.vo.ResVo;
import com.hnv99.forum.api.model.vo.user.dto.BaseUserInfoDTO;
import com.hnv99.forum.api.model.vo.user.dto.SimpleUserInfoDTO;
import com.hnv99.forum.core.permission.Permission;
import com.hnv99.forum.core.permission.UserRole;
import com.hnv99.forum.front.search.vo.SearchUserVo;
import com.hnv99.forum.service.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Backend for user permission management.
 * Manages user administration.
 *
 * @author LouZai
 * @date 2022/9/19
 */
@RestController
@Permission(role = UserRole.ADMIN)
@Api(value = "User management controller", tags = "User management")
@RequestMapping(path = {"api/admin/user/", "admin/user/"})
public class UserSettingRestController {

    @Autowired
    private UserService userService;

    /**
     * Search for users.
     *
     * @param key The search key.
     * @return A response containing a list of users matching the search criteria.
     */
    @ApiOperation("User search")
    @GetMapping(path = "query")
    public ResVo<SearchUserVo> queryUserList(@RequestParam(name = "key", required = false) String key) {
        List<SimpleUserInfoDTO> list = userService.searchUser(key);
        SearchUserVo vo = new SearchUserVo();
        vo.setKey(key);
        vo.setItems(list);
        return ResVo.ok(vo);
    }

    /**
     * Get information about the currently logged-in user.
     *
     * @return A response containing information about the currently logged-in user.
     */
    @Permission(role = UserRole.LOGIN)
    @ApiOperation("Get information about the currently logged-in user")
    @GetMapping("info")
    public ResVo<BaseUserInfoDTO> info() {
        BaseUserInfoDTO user = ReqInfoContext.getReqInfo().getUser();
        return ResVo.ok(user);
    }
}
