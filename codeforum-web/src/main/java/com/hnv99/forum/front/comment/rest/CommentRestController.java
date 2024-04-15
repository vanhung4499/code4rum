package com.hnv99.forum.front.comment.rest;

import com.hnv99.forum.api.model.context.ReqInfoContext;
import com.hnv99.forum.api.model.enums.DocumentTypeEnum;
import com.hnv99.forum.api.model.enums.NotifyTypeEnum;
import com.hnv99.forum.api.model.enums.OperateTypeEnum;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.ResVo;
import com.hnv99.forum.api.model.vo.comment.CommentSaveReq;
import com.hnv99.forum.api.model.vo.comment.dto.TopCommentDTO;
import com.hnv99.forum.api.model.vo.constants.StatusEnum;
import com.hnv99.forum.api.model.vo.notify.NotifyMsgEvent;
import com.hnv99.forum.component.TemplateEngineHelper;
import com.hnv99.forum.core.permission.Permission;
import com.hnv99.forum.core.permission.UserRole;
import com.hnv99.forum.core.util.NumUtil;
import com.hnv99.forum.core.util.SpringUtil;
import com.hnv99.forum.front.article.vo.ArticleDetailVo;
import com.hnv99.forum.service.article.converter.ArticleConverter;
import com.hnv99.forum.service.article.repository.entity.ArticleDO;
import com.hnv99.forum.service.article.service.ArticleReadService;
import com.hnv99.forum.service.comment.repository.entity.CommentDO;
import com.hnv99.forum.service.comment.service.CommentReadService;
import com.hnv99.forum.service.comment.service.CommentWriteService;
import com.hnv99.forum.service.user.repository.entity.UserFootDO;
import com.hnv99.forum.service.user.service.UserFootService;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Manages comments on articles.
 * Provides endpoints for listing, posting, deleting, and performing actions on comments.
 **/
@RestController
@RequestMapping(path = "comment/api")
public class CommentRestController {
    @Autowired
    private ArticleReadService articleReadService;

    @Autowired
    private CommentReadService commentReadService;

    @Autowired
    private CommentWriteService commentWriteService;

    @Autowired
    private UserFootService userFootService;

    @Autowired
    private TemplateEngineHelper templateEngineHelper;

    /**
     * Retrieves the list of comments for an article.
     *
     * @param articleId the ID of the article
     * @param pageNum   the page number
     * @param pageSize  the page size
     * @return the list of comments
     */
    @ResponseBody
    @RequestMapping(path = "list")
    public ResVo<List<TopCommentDTO>> list(Long articleId, Long pageNum, Long pageSize) {
        if (NumUtil.nullOrZero(articleId)) {
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "文章id为空");
        }
        pageNum = Optional.ofNullable(pageNum).orElse(PageParam.DEFAULT_PAGE_NUM);
        pageSize = Optional.ofNullable(pageSize).orElse(PageParam.DEFAULT_PAGE_SIZE);
        List<TopCommentDTO> result = commentReadService.getArticleComments(articleId, PageParam.newPageInstance(pageNum, pageSize));
        return ResVo.ok(result);
    }

    /**
     * Saves a new comment.
     *
     * @param req the request containing the comment details
     * @return the newly created comment
     */
    @Permission(role = UserRole.LOGIN)
    @PostMapping(path = "post")
    @ResponseBody
    public ResVo<String> save(@RequestBody CommentSaveReq req) {
        if (req.getArticleId() == null) {
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "文章id为空");
        }
        ArticleDO article = articleReadService.queryBasicArticle(req.getArticleId());
        if (article == null) {
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "文章不存在!");
        }

        // Save the comment
        req.setUserId(ReqInfoContext.getReqInfo().getUserId());
        req.setCommentContent(StringEscapeUtils.escapeHtml3(req.getCommentContent()));
        commentWriteService.saveComment(req);

        // Return the newly created comment
        ArticleDetailVo vo = new ArticleDetailVo();
        vo.setArticle(ArticleConverter.toDto(article));
        List<TopCommentDTO> comments = commentReadService.getArticleComments(req.getArticleId(), PageParam.newPageInstance());
        vo.setComments(comments);
        TopCommentDTO hotComment = commentReadService.queryHotComment(req.getArticleId());
        vo.setHotComment(hotComment);
        String content = templateEngineHelper.render("views/article-detail/comment/index", vo);
        return ResVo.ok(content);
    }

    /**
     * Deletes a comment.
     *
     * @param commentId the ID of the comment to delete
     * @return true if the comment was deleted successfully, false otherwise
     */
    @Permission(role = UserRole.LOGIN)
    @RequestMapping(path = "delete")
    public ResVo<Boolean> delete(Long commentId) {
        commentWriteService.deleteComment(commentId, ReqInfoContext.getReqInfo().getUserId());
        return ResVo.ok(true);
    }

    /**
     * Performs actions like favorite or like a comment.
     *
     * @param commendId the ID of the comment
     * @param type      the type of action to perform (e.g., like, favorite)
     * @return true if the action was performed successfully, false otherwise
     */
    @Permission(role = UserRole.LOGIN)
    @GetMapping(path = "favor")
    public ResVo<Boolean> favor(@RequestParam(name = "commentId") Long commendId,
                                @RequestParam(name = "type") Integer type) {
        OperateTypeEnum operate = OperateTypeEnum.fromCode(type);
        if (operate == OperateTypeEnum.EMPTY) {
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, type + "非法");
        }

        CommentDO comment = commentReadService.queryComment(commendId);
        if (comment == null) {
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "评论不存在!");
        }

        UserFootDO foot = userFootService.saveOrUpdateUserFoot(DocumentTypeEnum.COMMENT,
                commendId,
                comment.getUserId(),
                ReqInfoContext.getReqInfo().getUserId(),
                operate);
        NotifyTypeEnum notifyType = OperateTypeEnum.getNotifyType(operate);
        Optional.ofNullable(notifyType).ifPresent(notify -> SpringUtil.publishEvent(new NotifyMsgEvent<>(this, notify, foot)));
        return ResVo.ok(true);
    }

}
