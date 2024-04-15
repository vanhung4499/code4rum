package com.hnv99.forum.service.sidebar.service;

import com.hnv99.forum.api.model.vo.recommend.SideBarDTO;

import java.util.List;

/**
 * Service for managing sidebar information.
 *
 * Provides methods to query sidebar information for various sections of the application.
 */
public interface SidebarService {

    /**
     * Query sidebar information for the home page.
     *
     * @return List of sidebar DTOs for the home page
     */
    List<SideBarDTO> queryHomeSidebarList();

    /**
     * Query sidebar information for tutorials.
     *
     * @return List of sidebar DTOs for tutorials
     */
    List<SideBarDTO> queryColumnSidebarList();

    /**
     * Query sidebar information for article details.
     *
     * @param author    The ID of the article author
     * @param articleId The ID of the article
     * @return List of sidebar DTOs for the article details
     */
    List<SideBarDTO> queryArticleDetailSidebarList(Long author, Long articleId);

}

