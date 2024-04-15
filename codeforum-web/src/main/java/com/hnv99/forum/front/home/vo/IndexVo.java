package com.hnv99.forum.front.home.vo;

import com.hnv99.forum.api.model.vo.PageListVo;
import com.hnv99.forum.api.model.vo.article.dto.ArticleDTO;
import com.hnv99.forum.api.model.vo.article.dto.CategoryDTO;
import com.hnv99.forum.api.model.vo.recommend.CarouseDTO;
import com.hnv99.forum.api.model.vo.recommend.SideBarDTO;
import com.hnv99.forum.api.model.vo.user.dto.UserStatisticInfoDTO;
import lombok.Data;
import java.util.List;

/**
 * Represents the data model for the index page.
 * Contains information such as categories, top articles, user information, sidebar items, etc.
 */
@Data
public class IndexVo {

    /**
     * List of categories.
     */
    private List<CategoryDTO> categories;

    /**
     * The currently selected category.
     */
    private String currentCategory;

    /**
     * The ID of the currently selected category.
     */
    private Long categoryId;

    /**
     * List of top articles.
     */
    private List<ArticleDTO> topArticles;

    /**
     * Paginated list of articles.
     */
    private PageListVo<ArticleDTO> articles;

    /**
     * Information about the logged-in user.
     */
    private UserStatisticInfoDTO user;

    /**
     * Sidebar items.
     */
    private List<SideBarDTO> sideBarItems;

    /**
     * Carousel items for the homepage.
     */
    private List<CarouseDTO> homeCarouselList;
}
