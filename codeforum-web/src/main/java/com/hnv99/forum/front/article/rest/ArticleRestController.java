package com.hnv99.forum.front.article.rest;

import com.hnv99.forum.api.model.context.ReqInfoContext;
import com.hnv99.forum.api.model.enums.DocumentTypeEnum;
import com.hnv99.forum.api.model.enums.NotifyTypeEnum;
import com.hnv99.forum.api.model.enums.OperateTypeEnum;
import com.hnv99.forum.api.model.vo.*;
import com.hnv99.forum.api.model.vo.article.ArticlePostReq;
import com.hnv99.forum.api.model.vo.article.ContentPostReq;
import com.hnv99.forum.api.model.vo.article.dto.ArticleDTO;
import com.hnv99.forum.api.model.vo.article.dto.CategoryDTO;
import com.hnv99.forum.api.model.vo.article.dto.TagDTO;
import com.hnv99.forum.api.model.vo.constants.StatusEnum;
import com.hnv99.forum.api.model.vo.notify.NotifyMsgEvent;
import com.hnv99.forum.api.model.vo.user.dto.BaseUserInfoDTO;
import com.hnv99.forum.component.TemplateEngineHelper;
import com.hnv99.forum.core.common.CommonConstants;
import com.hnv99.forum.core.mdc.MdcDot;
import com.hnv99.forum.core.permission.Permission;
import com.hnv99.forum.core.permission.UserRole;
import com.hnv99.forum.core.util.JsonUtil;
import com.hnv99.forum.core.util.MarkdownConverter;
import com.hnv99.forum.core.util.SpringUtil;
import com.hnv99.forum.front.article.vo.ArticleDetailVo;
import com.hnv99.forum.service.article.repository.entity.ArticleDO;
import com.hnv99.forum.service.article.service.*;
import com.hnv99.forum.service.notify.service.RabbitmqService;
import com.hnv99.forum.service.user.repository.entity.UserFootDO;
import com.hnv99.forum.service.user.service.UserFootService;
import com.hnv99.forum.service.user.service.UserService;
import com.rabbitmq.client.BuiltinExchangeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

/**
 * Returns JSON formatted data
 */
@Slf4j
@RequestMapping(path = "article/api")
@RestController
public class ArticleRestController {
    @Autowired
    private ArticleReadService articleReadService;
    @Autowired
    private UserFootService userFootService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private TagService tagService;
    @Autowired
    private ArticleReadService articleService;
    @Autowired
    private ArticleWriteService articleWriteService;

    @Autowired
    private TemplateEngineHelper templateEngineHelper;

    @Autowired
    private ArticleRecommendService articleRecommendService;

    @Autowired
    private RabbitmqService rabbitmqService;

    @Autowired
    private UserService userService;

    /**
     * Article detail page
     * - Parameter parsing knowledge points
     * - fixme * [1.Get request parameter parsing posture summary | One gray learning](https://hhui.top/spring-web/01.request/01.190824-springboot series tutorial web section get request parameter analysis posture summary/)
     *
     * @param articleId
     * @return
     */
    @GetMapping("/data/detail/{articleId}")
    public ResVo<ArticleDetailVo> detail(@PathVariable(name = "articleId") Long articleId) throws IOException {
        ArticleDetailVo vo = new ArticleDetailVo();
        // Article-related information
        ArticleDTO articleDTO = articleService.queryFullArticleInfo(articleId, ReqInfoContext.getReqInfo().getUserId());
        // Convert to html format when returning to the frontend page
        articleDTO.setContent(MarkdownConverter.markdownToHtml(articleDTO.getContent()));
        vo.setArticle(articleDTO);

        // Author information
        BaseUserInfoDTO user = userService.queryBasicUserInfo(articleDTO.getAuthor());
        articleDTO.setAuthorName(user.getUserName());
        articleDTO.setAuthorAvatar(user.getPhoto());
        return ResVo.ok(vo);
    }

    /**
     * Related recommendations for articles
     *
     * @param articleId
     * @param page
     * @param size
     * @return
     */
    @RequestMapping(path = "recommend")
    @MdcDot(bizCode = "#articleId")
    public ResVo<NextPageHtmlVo> recommend(@RequestParam(value = "articleId") Long articleId,
                                           @RequestParam(name = "page") Long page,
                                           @RequestParam(name = "size", required = false) Long size) {
        size = Optional.ofNullable(size).orElse(PageParam.DEFAULT_PAGE_SIZE);
        size = Math.min(size, PageParam.DEFAULT_PAGE_SIZE);
        PageListVo<ArticleDTO> articles = articleRecommendService.relatedRecommend(articleId, PageParam.newPageInstance(page, size));
        String html = templateEngineHelper.renderToVo("views/article-detail/article/list", "articles", articles);
        return ResVo.ok(new NextPageHtmlVo(html, articles.getHasMore()));
    }

    /**
     * Query all tags
     *
     * @return
     */
    @PostMapping(path = "generateSummary")
    public ResVo<String> generateSummary(@RequestBody ContentPostReq req) {
        return ResVo.ok(articleService.generateSummary(req.getContent()));
    }

    /**
     * Query all tags
     *
     * @return
     */
    @GetMapping(path = "tag/list")
    public ResVo<PageVo<TagDTO>> queryTags(@RequestParam(name = "key", required = false) String key,
                                           @RequestParam(name = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                           @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        PageVo<TagDTO> tagDTOPageVo = tagService.queryTags(key, PageParam.newPageInstance(pageNumber, pageSize));
        return ResVo.ok(tagDTOPageVo);
    }

    /**
     * Get all categories
     *
     * @return
     */
    @GetMapping(path = "category/list")
    public ResVo<List<CategoryDTO>> getCategoryList(@RequestParam(name = "categoryId", required = false) Long categoryId,
                                                    @RequestParam(name = "ignoreNoArticles", required = false) Boolean ignoreNoArticles) {
        List<CategoryDTO> list = categoryService.loadAllCategories();
        if (Objects.equals(Boolean.TRUE, ignoreNoArticles)) {
            // Query the number of articles corresponding to all categories
            Map<Long, Long> articleCnt = articleService.queryArticleCountsByCategory();
            // Filter out categories with no articles
            list.removeIf(c -> articleCnt.getOrDefault(c.getCategoryId(), 0L) <= 0L);
        }
        list.forEach(c -> c.setSelected(c.getCategoryId().equals(categoryId)));
        return ResVo.ok(list);
    }


    /**
     * Favorite, like, and other related operations
     *
     * @param articleId
     * @param type      Values come from OperateTypeEnum#code
     * @return
     */
    @Permission(role = UserRole.LOGIN)
    @GetMapping(path = "favor")
    @MdcDot(bizCode = "#articleId")
    public ResVo<Boolean> favor(@RequestParam(name = "articleId") Long articleId,
                                @RequestParam(name = "type") Integer type) throws IOException, TimeoutException {
        if (log.isDebugEnabled()) {
            log.debug("Start liking: {}", type);
        }
        OperateTypeEnum operate = OperateTypeEnum.fromCode(type);
        if (operate == OperateTypeEnum.EMPTY) {
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, type + " is illegal");
        }

        // Article must exist
        ArticleDO article = articleReadService.queryBasicArticle(articleId);
        if (article == null) {
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "Article does not exist!");
        }

        UserFootDO foot = userFootService.saveOrUpdateUserFoot(DocumentTypeEnum.ARTICLE, articleId, article.getUserId(),
                ReqInfoContext.getReqInfo().getUserId(),
                operate);
        // Like, collect messages
        NotifyTypeEnum notifyType = OperateTypeEnum.getNotifyType(operate);

        // Like messages use RabbitMQ, others use Java's built-in messaging mechanism
        if (notifyType.equals(NotifyTypeEnum.PRAISE) && rabbitmqService.enabled()) {
            rabbitmqService.publishMsg(
                    CommonConstants.EXCHANGE_NAME_DIRECT,
                    BuiltinExchangeType.DIRECT,
                    CommonConstants.QUEUE_KEY_PRAISE,
                    JsonUtil.toStr(foot));
        } else {
            Optional.ofNullable(notifyType).ifPresent(notify -> SpringUtil.publishEvent(new NotifyMsgEvent<>(this, notify, foot)));
        }

        if (log.isDebugEnabled()) {
            log.info("End liking: {}", type);
        }
        return ResVo.ok(true);
    }


    /**
     * Post an article, redirect to the detail page afterwards
     * - There's a knowledge point about redirection here
     * - fixme Blog: * [5. Request redirection | One gray learning](https://hhui.top/spring-web/02.response/05.190929-springboot series tutorial web section redirection/)
     *
     * @return
     */
    @Permission(role = UserRole.LOGIN)
    @PostMapping(path = "post")
    @MdcDot(bizCode = "#req.articleId")
    public ResVo<Long> post(@RequestBody ArticlePostReq req, HttpServletResponse response) throws IOException {
        Long id = articleWriteService.saveArticle(req, ReqInfoContext.getReqInfo().getUserId());
        // If using backend redirection, you can use the following two strategies
//        return "redirect:/article/detail/" + id;
//        response.sendRedirect("/article/detail/" + id);
        // Here we use frontend redirection strategy
        return ResVo.ok(id);
    }


    /**
     * Article deletion
     *
     * @param articleId
     * @return
     */
    @Permission(role = UserRole.LOGIN)
    @RequestMapping(path = "delete")
    @MdcDot(bizCode = "#articleId")
    public ResVo<Boolean> delete(@RequestParam(value = "articleId") Long articleId) {
        articleWriteService.deleteArticle(articleId, ReqInfoContext.getReqInfo().getUserId());
        return ResVo.ok(true);
    }
}

