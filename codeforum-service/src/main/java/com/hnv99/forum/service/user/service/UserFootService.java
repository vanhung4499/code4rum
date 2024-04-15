package com.hnv99.forum.service.user.service;

import com.hnv99.forum.api.model.enums.DocumentTypeEnum;
import com.hnv99.forum.api.model.enums.OperateTypeEnum;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.user.dto.SimpleUserInfoDTO;
import com.hnv99.forum.api.model.vo.user.dto.UserFootStatisticDTO;
import com.hnv99.forum.service.comment.repository.entity.CommentDO;
import com.hnv99.forum.service.user.repository.entity.UserFootDO;

import java.util.List;

/**
 * Service interface for user footprints.
 */
public interface UserFootService {
    /**
     * Save or update user footprint status information.
     *
     * @param documentType    The type of document: blog or comment.
     * @param documentId      The ID of the document.
     * @param authorId        The ID of the author.
     * @param userId          The ID of the user performing the operation.
     * @param operateTypeEnum The type of operation: like, comment, collect, etc.
     * @return The saved or updated user footprint.
     */
    UserFootDO saveOrUpdateUserFoot(DocumentTypeEnum documentType, Long documentId, Long authorId, Long userId, OperateTypeEnum operateTypeEnum);

    /**
     * Save comment footprint.
     * 1. Set the user's record on the article as commented.
     * 2. If the comment is a reply to another comment, mark the parent comment as commented.
     *
     * @param comment             The comment to be saved.
     * @param articleAuthor       The author of the article.
     * @param parentCommentAuthor The author of the parent comment.
     */
    void saveCommentFoot(CommentDO comment, Long articleAuthor, Long parentCommentAuthor);

    /**
     * Remove comment footprint.
     *
     * @param comment             The comment to be removed.
     * @param articleAuthor       The author of the article.
     * @param parentCommentAuthor The author of the parent comment.
     */
    void removeCommentFoot(CommentDO comment, Long articleAuthor, Long parentCommentAuthor);

    /**
     * Query the list of articles read by the user.
     *
     * @param userId    The ID of the user.
     * @param pageParam The pagination parameters.
     * @return The list of article IDs.
     */
    List<Long> queryUserReadArticleList(Long userId, PageParam pageParam);

    /**
     * Query the list of articles bookmarked by the user.
     *
     * @param userId    The ID of the user.
     * @param pageParam The pagination parameters.
     * @return The list of article IDs.
     */
    List<Long> queryUserCollectionArticleList(Long userId, PageParam pageParam);

    /**
     * Query user information who liked the article.
     *
     * @param articleId The ID of the article.
     * @return The list of user information who liked the article.
     */
    List<SimpleUserInfoDTO> queryArticlePraisedUsers(Long articleId);

    /**
     * Query user record to determine if they liked, commented, or bookmarked.
     *
     * @param documentId The ID of the document.
     * @param type       The type of the document.
     * @param userId     The ID of the user.
     * @return The user footprint information.
     */
    UserFootDO queryUserFoot(Long documentId, Integer type, Long userId);

    /**
     * Get the foot count statistics.
     *
     * @return The statistics of user footprints.
     */
    UserFootStatisticDTO getFootCount();
}
