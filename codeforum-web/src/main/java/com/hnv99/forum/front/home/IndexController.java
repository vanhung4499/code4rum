package com.hnv99.forum.front.home;

import com.hnv99.forum.front.home.helper.IndexRecommendHelper;
import com.hnv99.forum.front.home.vo.IndexVo;
import com.hnv99.forum.global.BaseViewController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller for the index page.
 * Handles requests related to the homepage and login page.
 */
@Controller
public class IndexController extends BaseViewController {

    @Autowired
    private IndexRecommendHelper indexRecommendHelper;

    /**
     * Handles requests to the homepage and login page.
     *
     * @param model   The model object
     * @param request The HTTP servlet request
     * @return The view name
     */
    @GetMapping(path = {"/", "", "/index", "/login"})
    public String index(Model model, HttpServletRequest request) {
        String activeTab = request.getParameter("category");
        IndexVo vo = indexRecommendHelper.buildIndexVo(activeTab);
        model.addAttribute("vo", vo);
        return "views/home/index";
    }
}

