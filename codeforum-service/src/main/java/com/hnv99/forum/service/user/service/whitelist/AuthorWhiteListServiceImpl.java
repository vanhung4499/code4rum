package com.hnv99.forum.service.user.service.whitelist;

import com.hnv99.forum.api.model.vo.user.dto.BaseUserInfoDTO;
import com.hnv99.forum.core.cache.RedisClient;
import com.hnv99.forum.service.user.service.AuthorWhiteListService;
import com.hnv99.forum.service.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Service for managing author white lists.
 *
 * Allows users in the white list to directly publish articles.
 */
@Service
public class AuthorWhiteListServiceImpl implements AuthorWhiteListService {
    /**
     * Use Redis set to store the white list of users allowed to publish articles directly.
     */
    private static final String ARTICLE_WHITE_LIST = "auth_article_white_list";

    @Autowired
    private UserService userService;

    /**
     * Checks if the author is in the article white list.
     *
     * @param authorId The ID of the author
     * @return true if the author is in the white list, otherwise false
     */
    @Override
    public boolean authorInArticleWhiteList(Long authorId) {
        return RedisClient.sIsMember(ARTICLE_WHITE_LIST, authorId);
    }

    /**
     * Retrieves all users in the article white list.
     *
     * @return List of basic user information of users in the white list
     */
    public List<BaseUserInfoDTO> queryAllArticleWhiteListAuthors() {
        Set<Long> users = RedisClient.sGetAll(ARTICLE_WHITE_LIST, Long.class);
        if (CollectionUtils.isEmpty(users)) {
            return Collections.emptyList();
        }
        List<BaseUserInfoDTO> userInfos = userService.batchQueryBasicUserInfo(users);
        return userInfos;
    }

    /**
     * Adds an author to the article white list.
     *
     * @param userId The ID of the user to be added
     */
    public void addAuthorToArticleWhiteList(Long userId) {
        RedisClient.sPut(ARTICLE_WHITE_LIST, userId);
    }

    /**
     * Removes an author from the article white list.
     *
     * @param userId The ID of the user to be removed
     */
    public void removeAuthorFromArticleWhiteList(Long userId) {
        RedisClient.sDel(ARTICLE_WHITE_LIST, userId);
    }
}

