package com.hnv99.forum.front.user.rest;

import com.hnv99.forum.api.model.context.ReqInfoContext;
import com.hnv99.forum.api.model.enums.FollowTypeEnum;
import com.hnv99.forum.api.model.enums.HomeSelectEnum;
import com.hnv99.forum.api.model.vo.NextPageHtmlVo;
import com.hnv99.forum.api.model.vo.PageListVo;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.ResVo;
import com.hnv99.forum.api.model.vo.article.dto.ArticleDTO;
import com.hnv99.forum.api.model.vo.constants.StatusEnum;
import com.hnv99.forum.api.model.vo.user.UserInfoSaveReq;
import com.hnv99.forum.api.model.vo.user.UserRelationReq;
import com.hnv99.forum.api.model.vo.user.dto.FollowUserInfoDTO;
import com.hnv99.forum.component.TemplateEngineHelper;
import com.hnv99.forum.core.permission.Permission;
import com.hnv99.forum.core.permission.UserRole;
import com.hnv99.forum.service.article.service.ArticleReadService;
import com.hnv99.forum.service.user.service.relation.UserRelationServiceImpl;
import com.hnv99.forum.service.user.service.user.UserServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * Handles user-related API endpoints.
 */
@RestController
@RequestMapping(path = "user/api")
public class UserRestController {

    @Resource
    private UserServiceImpl userService;

    @Resource
    private UserRelationServiceImpl userRelationService;

    @Resource
    private TemplateEngineHelper templateEngineHelper;

    @Resource
    private ArticleReadService articleReadService;

    /**
     * Saves user relationship.
     *
     * @param req
     * @return
     * @throws Exception
     */
    @Permission(role = UserRole.LOGIN)
    @PostMapping(path = "saveUserRelation")
    public ResVo<Boolean> saveUserRelation(@RequestBody UserRelationReq req) {
        userRelationService.saveUserRelation(req);
        return ResVo.ok(true);
    }

    /**
     * Saves user details.
     *
     * @param req
     * @return
     * @throws Exception
     */
    @Permission(role = UserRole.LOGIN)
    @PostMapping(path = "saveUserInfo")
    @Transactional(rollbackFor = Exception.class)
    public ResVo<Boolean> saveUserInfo(@RequestBody UserInfoSaveReq req) {
        if (req.getUserId() == null || !Objects.equals(req.getUserId(), ReqInfoContext.getReqInfo().getUserId())) {
            // Cannot modify information of other users
            return ResVo.fail(StatusEnum.FORBID_ERROR_MIXED, "Unauthorized to modify");
        }
        userService.saveUserInfo(req);
        return ResVo.ok(true);
    }

    /**
     * Paginates through user's article list.
     *
     * @param userId
     * @param homeSelectType
     * @return
     */
    @GetMapping(path = "articleList")
    public ResVo<NextPageHtmlVo> articleList(@RequestParam(name = "userId") Long userId,
                                             @RequestParam(name = "homeSelectType") String homeSelectType,
                                             @RequestParam("page") Long page,
                                             @RequestParam(name = "pageSize", required = false) Long pageSize) {
        HomeSelectEnum select = HomeSelectEnum.fromCode(homeSelectType);
        if (select == null) {
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS);
        }

        if (pageSize == null) pageSize = PageParam.DEFAULT_PAGE_SIZE;
        PageParam pageParam = PageParam.newPageInstance(page, pageSize);
        PageListVo<ArticleDTO> dto = articleReadService.queryArticlesByUserAndType(userId, pageParam, select);
        String html = templateEngineHelper.renderToVo("views/user/articles/index", "homeSelectList", dto);
        return ResVo.ok(new NextPageHtmlVo(html, dto.getHasMore()));
    }

    @GetMapping(path = "followList")
    public ResVo<NextPageHtmlVo> followList(@RequestParam(name = "userId") Long userId,
                                            @RequestParam(name = "followSelectType") String followSelectType,
                                            @RequestParam("page") Long page,
                                            @RequestParam(name = "pageSize", required = false) Long pageSize) {
        if (pageSize == null) pageSize = PageParam.DEFAULT_PAGE_SIZE;
        PageParam pageParam = PageParam.newPageInstance(page, pageSize);
        PageListVo<FollowUserInfoDTO> followList;
        boolean needUpdateRelation = false;
        if (followSelectType.equals(FollowTypeEnum.FOLLOW.getCode())) {
            followList = userRelationService.getUserFollowList(userId, pageParam);
        } else {
            // When querying the list of fans, we can only determine if the fans are following the userId,
            // but cannot determine the reverse, so we need to update the mapping relationship to check if userId is following this user.
            followList = userRelationService.getUserFansList(userId, pageParam);
            needUpdateRelation = true;
        }

        Long loginUserId = ReqInfoContext.getReqInfo().getUserId();
        if (!Objects.equals(loginUserId, userId) || needUpdateRelation) {
            userRelationService.updateUserFollowRelationId(followList, userId);
        }
        String html = templateEngineHelper.renderToVo("views/user/follows/index", "followList", followList);
        return ResVo.ok(new NextPageHtmlVo(html, followList.getHasMore()));
    }
}
