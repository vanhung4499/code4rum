package com.hnv99.forum.front.search.view;

import com.hnv99.forum.front.home.helper.IndexRecommendHelper;
import com.hnv99.forum.front.home.vo.IndexVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Recommendation service interface.
 */
@Controller
public class SearchViewController {

    @Autowired
    private IndexRecommendHelper indexRecommendHelper;

    /**
     * Retrieves the article list.
     *
     * @param key   The search keyword
     * @param model The model object
     * @return The view name
     */
    @GetMapping(path = "search")
    public String searchArticleList(@RequestParam(name = "key") String key, Model model) {
        if (!StringUtils.isBlank(key)) {
            IndexVo vo = indexRecommendHelper.buildSearchVo(key);
            model.addAttribute("vo", vo);
        }
        return "views/article-search-list/index";
    }
}
