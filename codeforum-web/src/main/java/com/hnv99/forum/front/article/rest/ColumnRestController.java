package com.hnv99.forum.front.article.rest;

import com.hnv99.forum.api.model.vo.NextPageHtmlVo;
import com.hnv99.forum.api.model.vo.PageListVo;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.ResVo;
import com.hnv99.forum.api.model.vo.article.dto.ColumnDTO;
import com.hnv99.forum.api.model.vo.article.dto.SimpleArticleDTO;
import com.hnv99.forum.component.TemplateEngineHelper;
import com.hnv99.forum.service.article.service.ColumnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "column/api")
public class ColumnRestController {
    @Autowired
    private ColumnService columnService;

    @Autowired
    private TemplateEngineHelper templateEngineHelper;

    /**
     * Paginated list of columns
     *
     * @param page page number
     * @param size page size
     * @return
     */
    @GetMapping(path = "list")
    public ResVo<NextPageHtmlVo> list(@RequestParam(name = "page") Long page,
                                      @RequestParam(name = "size", required = false) Long size) {
        if (page <= 0) {
            page = 1L;
        }
        size = Optional.ofNullable(size).orElse(PageParam.DEFAULT_PAGE_SIZE);
        size = Math.min(size, PageParam.DEFAULT_PAGE_SIZE);
        PageListVo<ColumnDTO> list = columnService.listColumn(PageParam.newPageInstance(page, size));

        String html = templateEngineHelper.renderToVo("biz/column/list", "columns", list);
        return ResVo.ok(new NextPageHtmlVo(html, list.getHasMore()));
    }

    /**
     * Menu bar for the detail page (i.e., article list of the column)
     *
     * @param columnId
     * @return
     */
    @GetMapping(path = "menu/{column}")
    public ResVo<NextPageHtmlVo> columnMenus(@PathVariable("column") Long columnId) {
        List<SimpleArticleDTO> articleList = columnService.queryColumnArticles(columnId);
        String html = templateEngineHelper.renderToVo("biz/column/menus", "menu", articleList);
        return ResVo.ok(new NextPageHtmlVo(html, false));
    }
}

