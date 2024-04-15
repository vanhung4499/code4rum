package com.hnv99.forum.admin.rest;

import com.hnv99.forum.api.model.context.ReqInfoContext;
import com.hnv99.forum.api.model.enums.OperateArticleEnum;
import com.hnv99.forum.api.model.vo.PageVo;
import com.hnv99.forum.api.model.vo.ResVo;
import com.hnv99.forum.api.model.vo.article.ArticlePostReq;
import com.hnv99.forum.api.model.vo.article.SearchArticleReq;
import com.hnv99.forum.api.model.vo.article.dto.ArticleAdminDTO;
import com.hnv99.forum.api.model.vo.article.dto.ArticleDTO;
import com.hnv99.forum.api.model.vo.article.dto.SimpleArticleDTO;
import com.hnv99.forum.api.model.vo.constants.StatusEnum;
import com.hnv99.forum.core.permission.Permission;
import com.hnv99.forum.core.permission.UserRole;
import com.hnv99.forum.core.util.NumUtil;
import com.hnv99.forum.front.search.vo.SearchArticleVo;
import com.hnv99.forum.service.article.service.ArticleReadService;
import com.hnv99.forum.service.article.service.ArticleSettingService;
import com.hnv99.forum.service.article.service.ArticleWriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;

/**
 * ArticleSettingRestController class for managing article settings.
 */
@RestController
@Permission(role = UserRole.LOGIN)
@Api(value = "Article Setting Management Controller", tags = "Article Management")
@RequestMapping(path = {"/api/admin/article/", "/admin/article/"})
public class ArticleSettingRestController {

    @Autowired
    private ArticleSettingService articleSettingService;

    @Autowired
    private ArticleReadService articleReadService;

    @Autowired
    private ArticleWriteService articleWriteService;

    /**
     * Save an article.
     *
     * @param req The request body containing article information.
     * @return The response.
     */
    @Permission(role = UserRole.ADMIN)
    @PostMapping(path = "save")
    public ResVo<String> save(@RequestBody ArticlePostReq req) {
        if (NumUtil.nullOrZero(req.getArticleId())) {
            // Add new article
            this.articleWriteService.saveArticle(req, ReqInfoContext.getReqInfo().getUserId());
        } else {
            this.articleWriteService.saveArticle(req, null);
        }
        return ResVo.ok("ok");
    }

    /**
     * Update an article.
     *
     * @param req The request body containing updated article information.
     * @return The response.
     */
    @Permission(role = UserRole.ADMIN)
    @PostMapping(path = "update")
    public ResVo<String> update(@RequestBody ArticlePostReq req) {
        articleSettingService.updateArticle(req);
        return ResVo.ok("ok");
    }

    /**
     * Operate on an article (e.g., publish, unpublish).
     *
     * @param articleId    The ID of the article.
     * @param operateType  The type of operation to perform.
     * @return The response.
     */
    @Permission(role = UserRole.ADMIN)
    @GetMapping(path = "operate")
    public ResVo<String> operate(@RequestParam(name = "articleId") Long articleId, @RequestParam(name = "operateType") Integer operateType) {
        OperateArticleEnum operate = OperateArticleEnum.fromCode(operateType);
        if (operate == OperateArticleEnum.EMPTY) {
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, operateType + " is illegal");
        }
        articleSettingService.operateArticle(articleId, operate);
        return ResVo.ok("ok");
    }

    /**
     * Delete an article.
     *
     * @param articleId The ID of the article to delete.
     * @return The response.
     */
    @Permission(role = UserRole.ADMIN)
    @GetMapping(path = "delete")
    public ResVo<String> delete(@RequestParam(name = "articleId") Long articleId) {
        articleSettingService.deleteArticle(articleId);
        return ResVo.ok("ok");
    }

    /**
     * Get details of an article by ID.
     *
     * @param articleId The ID of the article.
     * @return The response containing article details.
     */
    @ApiOperation("Get details of an article by ID")
    @GetMapping(path = "detail")
    public ResVo<ArticleDTO> detail(@RequestParam(name = "articleId", required = false) Long articleId) {
        ArticleDTO articleDTO = new ArticleDTO();
        if (articleId != null) {
            // Query article details
            articleDTO = articleReadService.queryDetailArticleInfo(articleId);
        }

        return ResVo.ok(articleDTO);
    }

    /**
     * Get a list of articles.
     *
     * @param req The request containing search criteria.
     * @return The response containing a list of articles.
     */
    @ApiOperation("Get a list of articles")
    @PostMapping(path = "list")
    public ResVo<PageVo<ArticleAdminDTO>> list(@RequestBody SearchArticleReq req) {
        PageVo<ArticleAdminDTO> articleDTOPageVo = articleSettingService.getArticleList(req);
        return ResVo.ok(articleDTOPageVo);
    }

    /**
     * Search for articles.
     *
     * @param key The search key.
     * @return The response containing search results.
     */
    @ApiOperation("Search for articles")
    @GetMapping(path = "query")
    public ResVo<SearchArticleVo> queryArticleList(@RequestParam(name = "key", required = false) String key) {
        List<SimpleArticleDTO> list = articleReadService.querySimpleArticleBySearchKey(key);
        SearchArticleVo vo = new SearchArticleVo();
        vo.setKey(key);
        vo.setItems(list);
        return ResVo.ok(vo);
    }
}

