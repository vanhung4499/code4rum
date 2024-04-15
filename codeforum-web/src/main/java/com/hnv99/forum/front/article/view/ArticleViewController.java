package com.hnv99.forum.front.article.view;

import com.hnv99.forum.api.model.context.ReqInfoContext;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.article.dto.ArticleDTO;
import com.hnv99.forum.api.model.vo.article.dto.ArticleOtherDTO;
import com.hnv99.forum.api.model.vo.article.dto.CategoryDTO;
import com.hnv99.forum.api.model.vo.comment.dto.TopCommentDTO;
import com.hnv99.forum.api.model.vo.recommend.SideBarDTO;
import com.hnv99.forum.api.model.vo.user.dto.UserStatisticInfoDTO;
import com.hnv99.forum.core.permission.Permission;
import com.hnv99.forum.core.permission.UserRole;
import com.hnv99.forum.core.util.MarkdownConverter;
import com.hnv99.forum.core.util.SpringUtil;
import com.hnv99.forum.front.article.vo.ArticleDetailVo;
import com.hnv99.forum.front.article.vo.ArticleEditVo;
import com.hnv99.forum.global.BaseViewController;
import com.hnv99.forum.global.SeoInjectService;
import com.hnv99.forum.service.article.repository.entity.ColumnArticleDO;
import com.hnv99.forum.service.article.service.ArticleReadService;
import com.hnv99.forum.service.article.service.CategoryService;
import com.hnv99.forum.service.article.service.ColumnService;
import com.hnv99.forum.service.article.service.TagService;
import com.hnv99.forum.service.comment.service.CommentReadService;
import com.hnv99.forum.service.sidebar.service.SidebarService;
import com.hnv99.forum.service.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Article
 * TODO: Putting all entry points in one Controller will lead to very confusing functionality division
 * ： Article List
 * ： Article Editing
 * ： Article Details
 * ---
 * - Return View view
 * - Return JSON data
 */
@Controller
@RequestMapping(path = "article")
public class ArticleViewController extends BaseViewController {
    @Autowired
    private ArticleReadService articleService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TagService tagService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentReadService commentService;

    @Autowired
    private SidebarService sidebarService;

    @Autowired
    private ColumnService columnService;

    /**
     * Article editing page
     *
     * @param articleId
     * @return
     */
    @Permission(role = UserRole.LOGIN)
    @GetMapping(path = "edit")
    public String edit(@RequestParam(required = false) Long articleId, Model model) {
        ArticleEditVo vo = new ArticleEditVo();
        if (articleId != null) {
            ArticleDTO article = articleService.queryDetailArticleInfo(articleId);
            vo.setArticle(article);
            if (!Objects.equals(article.getAuthor(), ReqInfoContext.getReqInfo().getUserId())) {
                // No permission
                model.addAttribute("toast", "Content does not exist");
                return "redirect:403";
            }

            List<CategoryDTO> categoryList = categoryService.loadAllCategories();
            categoryList.forEach(s -> {
                s.setSelected(s.getCategoryId().equals(article.getCategory().getCategoryId()));
            });
            vo.setCategories(categoryList);
            vo.setTags(article.getTags());
        } else {
            List<CategoryDTO> categoryList = categoryService.loadAllCategories();
            vo.setCategories(categoryList);
            vo.setTags(Collections.emptyList());
        }
        model.addAttribute("vo", vo);
        return "views/article-edit/index";
    }


    /**
     * Article detail page
     * - Parameter parsing knowledge points
     * - fixme * [1.Get request parameter parsing posture summary | One gray learning](https://hhui.top/spring-web/01.request/01.190824-springboot series tutorial web section get request parameter analysis posture summary/)
     *
     * @param articleId
     * @return
     */
    @GetMapping("detail/{articleId}")
    public String detail(@PathVariable(name = "articleId") Long articleId, Model model) throws IOException {
        // Redirect for column articles
        ColumnArticleDO columnArticle = columnService.getColumnArticleRelation(articleId);
        if (columnArticle != null) {
            return String.format("redirect:/column/%d/%d", columnArticle.getColumnId(), columnArticle.getSection());
        }

        ArticleDetailVo vo = new ArticleDetailVo();
        // Article-related information
        ArticleDTO articleDTO = articleService.queryFullArticleInfo(articleId, ReqInfoContext.getReqInfo().getUserId());
        // Convert to html format when returning to the frontend page
        articleDTO.setContent(MarkdownConverter.markdownToHtml(articleDTO.getContent()));
        vo.setArticle(articleDTO);

        // Comment information
        List<TopCommentDTO> comments = commentService.getArticleComments(articleId, PageParam.newPageInstance(1L, 10L));
        vo.setComments(comments);

        // Hot comments
        TopCommentDTO hotComment = commentService.queryHotComment(articleId);
        vo.setHotComment(hotComment);

        // Other information packaging
        ArticleOtherDTO other = new ArticleOtherDTO();
        // Author information
        UserStatisticInfoDTO user = userService.queryUserInfoWithStatistic(articleDTO.getAuthor());
        articleDTO.setAuthorName(user.getUserName());
        articleDTO.setAuthorAvatar(user.getPhoto());
        vo.setAuthor(user);

        vo.setOther(other);

        // Sidebar recommendation information for detail pages
        List<SideBarDTO> sideBars = sidebarService.queryArticleDetailSidebarList(articleDTO.getAuthor(), articleDTO.getArticleId());
        vo.setSideBarItems(sideBars);
        model.addAttribute("vo", vo);

        SpringUtil.getBean(SeoInjectService.class).initColumnSeo(vo);
        return "views/article-detail/index";
    }


}
