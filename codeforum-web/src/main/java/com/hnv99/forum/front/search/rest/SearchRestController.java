package com.hnv99.forum.front.search.rest;

import com.hnv99.forum.api.model.vo.NextPageHtmlVo;
import com.hnv99.forum.api.model.vo.PageListVo;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.ResVo;
import com.hnv99.forum.api.model.vo.article.dto.ArticleDTO;
import com.hnv99.forum.api.model.vo.article.dto.SimpleArticleDTO;
import com.hnv99.forum.component.TemplateEngineHelper;
import com.hnv99.forum.front.search.vo.SearchArticleVo;
import com.hnv99.forum.global.BaseViewController;
import com.hnv99.forum.service.article.service.ArticleReadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Recommendation service interface.
 */
@RequestMapping(path = "search/api")
@RestController
public class SearchRestController extends BaseViewController {

    @Autowired
    private ArticleReadService articleReadService;

    @Autowired
    private TemplateEngineHelper templateEngineHelper;

    /**
     * Provides search suggestions based on the keyword.
     *
     * @param key The search keyword
     */
    @GetMapping(path = "hint")
    public ResVo<SearchArticleVo> recommend(@RequestParam(name = "key", required = false) String key) {
        List<SimpleArticleDTO> list = articleReadService.querySimpleArticleBySearchKey(key);
        SearchArticleVo vo = new SearchArticleVo();
        vo.setKey(key);
        vo.setItems(list);
        return ResVo.ok(vo);
    }

    /**
     * Retrieves a list of articles based on the search key.
     *
     * @param key  The search keyword
     * @param page The page number
     * @param size The page size
     * @return ResVo containing the NextPageHtmlVo
     */
    @GetMapping(path = "list")
    public ResVo<NextPageHtmlVo> searchList(@RequestParam(name = "key", required = false) String key,
                                            @RequestParam(name = "page") Long page,
                                            @RequestParam(name = "size", required = false) Long size) {
        PageParam pageParam = buildPageParam(page, size);
        PageListVo<ArticleDTO> list = articleReadService.queryArticlesBySearchKey(key, pageParam);
        String html = templateEngineHelper.renderToVo("views/article-search-list/article/list", "articles", list);
        return ResVo.ok(new NextPageHtmlVo(html, list.getHasMore()));
    }
}

