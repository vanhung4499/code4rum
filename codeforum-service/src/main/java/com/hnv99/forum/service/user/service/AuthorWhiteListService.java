package com.hnv99.forum.service.user.service;

import com.hnv99.forum.api.model.vo.user.dto.BaseUserInfoDTO;

import java.util.List;

/**
 * Service interface for managing author white list.
 */
public interface AuthorWhiteListService {

    /**
     * Checks if the author is in the white list for article publication.
     * This white list is mainly used to control whether authors need to be reviewed after publishing articles.
     *
     * @param authorId the ID of the author
     * @return true if the author is in the white list, false otherwise
     */
    boolean authorInArticleWhiteList(Long authorId);

    /**
     * Retrieves all users in the article white list.
     *
     * @return a list of users in the article white list
     */
    List<BaseUserInfoDTO> queryAllArticleWhiteListAuthors();

    /**
     * Adds a user to the article white list.
     *
     * @param userId the ID of the user to be added to the white list
     */
    void addAuthorToArticleWhiteList(Long userId);

    /**
     * Removes a user from the article white list.
     *
     * @param userId the ID of the user to be removed from the white list
     */
    void removeAuthorFromArticleWhiteList(Long userId);
}

