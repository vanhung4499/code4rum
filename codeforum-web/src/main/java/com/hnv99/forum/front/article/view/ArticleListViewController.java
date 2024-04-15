package com.hnv99.forum.front.article.view;

import com.hnv99.forum.api.model.vo.PageListVo;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.article.dto.ArticleDTO;
import com.hnv99.forum.front.article.vo.ArticleListVo;
import com.hnv99.forum.global.BaseViewController;
import com.hnv99.forum.service.article.service.ArticleReadService;
import com.hnv99.forum.service.article.service.CategoryService;
import com.hnv99.forum.service.article.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Article list view
 */
@RequestMapping(path = "article")
@Controller
public class ArticleListViewController extends BaseViewController {
    @Autowired
    private ArticleReadService articleService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private TagService tagService;

    /**
     * Query the list of articles under a certain category
     *
     * @param category
     * @return
     */
    @GetMapping(path = "category/{category}")
    public String categoryList(@PathVariable("category") String category, Model model) {
        Long categoryId = categoryService.queryCategoryId(category);
        PageListVo<ArticleDTO> list = categoryId != null ? articleService.queryArticlesByCategory(categoryId, PageParam.newPageInstance()) : PageListVo.emptyVo();
        ArticleListVo vo = new ArticleListVo();
        vo.setArchives(category);
        vo.setArchiveId(categoryId);
        vo.setArticles(list);
        model.addAttribute("vo", vo);
        return "views/article-category-list/index";
    }

    /**
     * Query the list of articles under a certain tag
     *
     * @param tag
     * @param model
     * @return
     */
    @GetMapping(path = "tag/{tag}")
    public String tagList(@PathVariable("tag") String tag, Model model) {
        Long tagId = tagService.queryTagId(tag);
        PageListVo<ArticleDTO> list = tagId != null ? articleService.queryArticlesByTag(tagId, PageParam.newPageInstance()) : PageListVo.emptyVo();
        ArticleListVo vo = new ArticleListVo();
        vo.setArchives(tag);
        vo.setArchiveId(tagId);
        vo.setArticles(list);
        model.addAttribute("vo", vo);
        return "views/article-tag-list/index";
    }
}

