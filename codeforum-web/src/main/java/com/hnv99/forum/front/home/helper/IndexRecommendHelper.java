package com.hnv99.forum.front.home.helper;

import com.hnv99.forum.api.model.context.ReqInfoContext;
import com.hnv99.forum.api.model.enums.ConfigTypeEnum;
import com.hnv99.forum.api.model.vo.PageListVo;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.article.dto.ArticleDTO;
import com.hnv99.forum.api.model.vo.article.dto.CategoryDTO;
import com.hnv99.forum.api.model.vo.banner.dto.ConfigDTO;
import com.hnv99.forum.api.model.vo.recommend.CarouseDTO;
import com.hnv99.forum.api.model.vo.user.dto.UserStatisticInfoDTO;
import com.hnv99.forum.core.async.AsyncUtil;
import com.hnv99.forum.core.common.CommonConstants;
import com.hnv99.forum.front.home.vo.IndexVo;
import com.hnv99.forum.service.article.service.ArticleReadService;
import com.hnv99.forum.service.article.service.CategoryService;
import com.hnv99.forum.service.config.service.ConfigService;
import com.hnv99.forum.service.sidebar.service.SidebarService;
import com.hnv99.forum.service.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Helper class for generating data for the index page.
 * Contains methods to build the index page view object based on active tab and search key.
 * Provides functionality to fetch categories, top articles, user information, sidebar items, and carousel items.
 */
@Component
public class IndexRecommendHelper {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ArticleReadService articleService;

    @Autowired
    private UserService userService;

    @Autowired
    private SidebarService sidebarService;

    @Autowired
    private ConfigService configService;

    /**
     * Builds the view object for the index page.
     *
     * @param activeTab The active tab selected by the user.
     * @return The IndexVo object containing data for the index page.
     */
    public IndexVo buildIndexVo(String activeTab) {
        IndexVo vo = new IndexVo();
        CategoryDTO category = categories(activeTab, vo);
        vo.setCategoryId(category.getCategoryId());
        vo.setCurrentCategory(category.getCategory());

        AsyncUtil.concurrentExecutor("Homepage Response")
                .runAsyncWithTimeRecord(() -> vo.setArticles(articleList(category.getCategoryId())), "Article List")
                .runAsyncWithTimeRecord(() -> vo.setTopArticles(topArticleList(category)), "Top Articles")
                .runAsyncWithTimeRecord(() -> vo.setHomeCarouselList(homeCarouselList()), "Carousel Images")
                .runAsyncWithTimeRecord(() -> vo.setSideBarItems(sidebarService.queryHomeSidebarList()), "Sidebar Items")
                .runAsyncWithTimeRecord(() -> vo.setUser(loginInfo()), "User Information")
                .allExecuted()
                .prettyPrint();

        return vo;
    }

    /**
     * Builds the view object for the search page.
     *
     * @param key The search key entered by the user.
     * @return The IndexVo object containing data for the search page.
     */
    public IndexVo buildSearchVo(String key) {
        IndexVo vo = new IndexVo();
        vo.setArticles(articleService.queryArticlesBySearchKey(key, PageParam.newPageInstance()));
        vo.setSideBarItems(sidebarService.queryHomeSidebarList());
        return vo;
    }

    /**
     * Retrieves the carousel images for the homepage.
     *
     * @return The list of CarouseDTO objects representing carousel images.
     */
    private List<CarouseDTO> homeCarouselList() {
        List<ConfigDTO> configList = configService.getConfigList(ConfigTypeEnum.HOME_PAGE);
        return configList.stream()
                .map(configDTO -> new CarouseDTO()
                        .setName(configDTO.getName())
                        .setImgUrl(configDTO.getBannerUrl())
                        .setActionUrl(configDTO.getJumpUrl()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the paginated list of articles based on the category ID.
     *
     * @param categoryId The ID of the category.
     * @return The paginated list of articles.
     */
    private PageListVo<ArticleDTO> articleList(Long categoryId) {
        return articleService.queryArticlesByCategory(categoryId, PageParam.newPageInstance());
    }

    /**
     * Retrieves the list of top articles for the given category.
     *
     * @param category The category for which to retrieve top articles.
     * @return The list of top articles.
     */
    private List<ArticleDTO> topArticleList(CategoryDTO category) {
        List<ArticleDTO> topArticles = articleService.queryTopArticlesByCategory(category.getCategoryId() == 0 ? null : category.getCategoryId());
        if (topArticles.size() < PageParam.TOP_PAGE_SIZE) {
            topArticles.clear();
            return topArticles;
        }

        List<String> topPicList = CommonConstants.HOMEPAGE_TOP_PIC_MAP.getOrDefault(category.getCategory(),
                CommonConstants.HOMEPAGE_TOP_PIC_MAP.get(CommonConstants.CATEGORY_ALL));

        AtomicInteger index = new AtomicInteger(0);
        topArticles.forEach(s -> s.setCover(topPicList.get(index.getAndIncrement() % topPicList.size())));
        return topArticles;
    }

    /**
     * Retrieves the list of categories and refreshes the selected category.
     *
     * @param active The active tab selected by the user.
     * @param vo The IndexVo object to which the categories and selected category are assigned.
     * @return The selected category; returns the default "All" category if no match is found.
     */
    private CategoryDTO categories(String active, IndexVo vo) {
        List<CategoryDTO> allList = categoryService.loadAllCategories();
        Map<Long, Long> articleCnt = articleService.queryArticleCountsByCategory();
        allList.removeIf(c -> articleCnt.getOrDefault(c.getCategoryId(), 0L) <= 0L);

        AtomicReference<CategoryDTO> selectedArticle = new AtomicReference<>();
        allList.forEach(category -> {
            if (category.getCategory().equalsIgnoreCase(active)) {
                category.setSelected(true);
                selectedArticle.set(category);
            } else {
                category.setSelected(false);
            }
        });

        allList.add(0, new CategoryDTO(0L, CategoryDTO.DEFAULT_TOTAL_CATEGORY));
        if (selectedArticle.get() == null) {
            selectedArticle.set(allList.get(0));
            allList.get(0).setSelected(true);
        }

        vo.setCategories(allList);
        return selectedArticle.get();
    }

    /**
     * Retrieves user information based on the current login.
     *
     * @return The UserStatisticInfoDTO object representing user information.
     */
    private UserStatisticInfoDTO loginInfo() {
        if (ReqInfoContext.getReqInfo() != null && ReqInfoContext.getReqInfo().getUserId() != null) {
            return userService.queryUserInfoWithStatistic(ReqInfoContext.getReqInfo().getUserId());
        }
        return null;
    }
}

