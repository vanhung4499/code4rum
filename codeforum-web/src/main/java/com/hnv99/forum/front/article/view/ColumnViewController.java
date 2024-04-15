package com.hnv99.forum.front.article.view;

import com.hnv99.forum.api.model.context.ReqInfoContext;
import com.hnv99.forum.api.model.enums.column.ColumnArticleReadEnum;
import com.hnv99.forum.api.model.enums.column.ColumnTypeEnum;
import com.hnv99.forum.api.model.vo.PageListVo;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.article.dto.*;
import com.hnv99.forum.api.model.vo.comment.dto.TopCommentDTO;
import com.hnv99.forum.api.model.vo.recommend.SideBarDTO;
import com.hnv99.forum.config.GlobalViewConfig;
import com.hnv99.forum.core.util.MarkdownConverter;
import com.hnv99.forum.core.util.SpringUtil;
import com.hnv99.forum.front.article.vo.ColumnVo;
import com.hnv99.forum.global.SeoInjectService;
import com.hnv99.forum.service.article.repository.entity.ColumnArticleDO;
import com.hnv99.forum.service.article.service.ArticleReadService;
import com.hnv99.forum.service.article.service.ColumnService;
import com.hnv99.forum.service.comment.service.CommentReadService;
import com.hnv99.forum.service.sidebar.service.SidebarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.List;

/**
 * Controller for managing columns and related operations.
 * Provides endpoints for listing columns, viewing column details, and reading articles within a column.
 */
@Controller
@RequestMapping(path = "column")
public class ColumnViewController {
    @Autowired
    private ColumnService columnService;
    @Autowired
    private ArticleReadService articleReadService;
    @Autowired
    private CommentReadService commentReadService;
    @Autowired
    private SidebarService sidebarService;
    @Resource
    private GlobalViewConfig globalViewConfig;

    /**
     * Displays the main page of columns, showing a list of columns.
     *
     * @param model the model to be populated with data
     * @return the view name
     */
    @GetMapping(path = {"list", "/", "", "home"})
    public String list(Model model) {
        PageListVo<ColumnDTO> columns = columnService.listColumn(PageParam.newPageInstance());
        List<SideBarDTO> sidebars = sidebarService.queryColumnSidebarList();
        ColumnVo vo = new ColumnVo();
        vo.setColumns(columns);
        vo.setSideBarItems(sidebars);
        model.addAttribute("vo", vo);
        return "views/column-home/index";
    }

    /**
     * Displays the details of a column.
     *
     * @param columnId the ID of the column
     * @param model    the model to be populated with data
     * @return the view name
     */
    @GetMapping(path = "{columnId}")
    public String column(@PathVariable("columnId") Long columnId, Model model) {
        ColumnDTO dto = columnService.queryColumnInfo(columnId);
        model.addAttribute("vo", dto);
        return "/views/column-index/index";
    }

    /**
     * Displays the article reading interface for a column.
     *
     * @param columnId the ID of the column
     * @param section  the section number (starting from 1)
     * @param model    the model to be populated with data
     * @return the view name
     */
    @GetMapping(path = "{columnId}/{section}")
    public String articles(@PathVariable("columnId") Long columnId, @PathVariable("section") Integer section, Model model) {
        if (section <= 0) section = 1;
        ColumnDTO column = columnService.queryBasicColumnInfo(columnId);
        ColumnArticleDO columnArticle = columnService.queryColumnArticle(columnId, section);
        Long articleId = columnArticle.getArticleId();
        ArticleDTO articleDTO = articleReadService.queryFullArticleInfo(articleId, ReqInfoContext.getReqInfo().getUserId());
        articleDTO.setContent(MarkdownConverter.markdownToHtml(articleDTO.getContent()));
        List<TopCommentDTO> comments = commentReadService.getArticleComments(articleId, PageParam.newPageInstance());
        TopCommentDTO hotComment = commentReadService.queryHotComment(articleId);
        List<SimpleArticleDTO> articles = columnService.queryColumnArticles(columnId);

        ColumnArticlesDTO vo = new ColumnArticlesDTO();
        vo.setArticle(articleDTO);
        vo.setComments(comments);
        vo.setHotComment(hotComment);
        vo.setColumn(columnId);
        vo.setSection(section);
        vo.setArticleList(articles);
        ArticleOtherDTO other = new ArticleOtherDTO();
        updateReadType(other, column, articleDTO, ColumnArticleReadEnum.valueOf(columnArticle.getReadType()));

        ColumnArticleFlipDTO flip = new ColumnArticleFlipDTO();
        flip.setPrevHref("/column/" + columnId + "/" + (section - 1));
        flip.setPrevShow(section > 1);
        flip.setNextHref("/column/" + columnId + "/" + (section + 1));
        flip.setNextShow(section < articles.size());
        other.setFlip(flip);

        vo.setOther(other);
        model.addAttribute("vo", vo);

        SpringUtil.getBean(SeoInjectService.class).initColumnSeo(vo, column);
        return "views/column-detail/index";
    }

    /**
     * Updates the read type of the article.
     *
     * @param vo           the ArticleOtherDTO object
     * @param column       the ColumnDTO object
     * @param articleDTO   the ArticleDTO object
     * @param articleReadEnum the read type enum value
     */
    private void updateReadType(ArticleOtherDTO vo, ColumnDTO column, ArticleDTO articleDTO, ColumnArticleReadEnum articleReadEnum) {
        Long loginUser = ReqInfoContext.getReqInfo().getUserId();
        if (loginUser != null && loginUser.equals(articleDTO.getAuthor())) {
            vo.setReadType(ColumnTypeEnum.FREE.getType());
            return;
        }

        if (articleReadEnum == ColumnArticleReadEnum.COLUMN_TYPE) {
            if (column.getType() == ColumnTypeEnum.TIME_FREE.getType()) {
                long now = System.currentTimeMillis();
                if (now > column.getFreeEndTime() || now < column.getFreeStartTime()) {
                    vo.setReadType(ColumnTypeEnum.LOGIN.getType());
                } else {
                    vo.setReadType(ColumnTypeEnum.FREE.getType());
                }
            } else {
                vo.setReadType(column.getType());
            }
        } else {
            vo.setReadType(articleReadEnum.getRead());
        }
        articleDTO.setContent(trimContent(vo.getReadType(), articleDTO.getContent()));
    }

    /**
     * Hides the article content based on the read type.
     *
     * @param readType the read type
     * @param content  the content to be trimmed
     * @return the trimmed content
     */
    private String trimContent(int readType, String content) {
        if (readType == ColumnTypeEnum.STAR_READ.getType()) {
            if (ReqInfoContext.getReqInfo().getUser() != null) {
                return content;
            }
            int count = Integer.parseInt(globalViewConfig.getZsxqArticleReadCount());
            return content.substring(0, content.length() * count / 100);
        }

        if ((readType == ColumnTypeEnum.LOGIN.getType() && ReqInfoContext.getReqInfo().getUserId() == null)) {
            int count = Integer.parseInt(globalViewConfig.getNeedLoginArticleReadCount());
            return content.substring(0, content.length() * count / 100);
        }

        return content;
    }
}

