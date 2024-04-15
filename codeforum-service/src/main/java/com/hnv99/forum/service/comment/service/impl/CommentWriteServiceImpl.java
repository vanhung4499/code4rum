package com.hnv99.forum.service.comment.service.impl;

import com.hnv99.forum.api.model.enums.NotifyTypeEnum;
import com.hnv99.forum.api.model.enums.YesOrNoEnum;
import com.hnv99.forum.api.model.exception.ExceptionUtil;
import com.hnv99.forum.api.model.vo.comment.CommentSaveReq;
import com.hnv99.forum.api.model.vo.constants.StatusEnum;
import com.hnv99.forum.api.model.vo.notify.NotifyMsgEvent;
import com.hnv99.forum.core.util.NumUtil;
import com.hnv99.forum.core.util.SpringUtil;
import com.hnv99.forum.service.article.repository.entity.ArticleDO;
import com.hnv99.forum.service.article.service.ArticleReadService;
import com.hnv99.forum.service.comment.converter.CommentConverter;
import com.hnv99.forum.service.comment.repository.dao.CommentDao;
import com.hnv99.forum.service.comment.repository.entity.CommentDO;
import com.hnv99.forum.service.comment.service.CommentWriteService;
import com.hnv99.forum.service.user.service.UserFootService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;

/**
 * Comment Write Service Implementation
 */
@Service
public class CommentWriteServiceImpl implements CommentWriteService {

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private ArticleReadService articleReadService;

    @Autowired
    private UserFootService userFootWriteService;

    /**
     * Save or update a comment
     *
     * @param commentSaveReq The CommentSaveReq object containing the comment information
     * @return The ID of the saved or updated comment
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveComment(CommentSaveReq commentSaveReq) {
        CommentDO comment;
        if (NumUtil.nullOrZero(commentSaveReq.getCommentId())) {
            comment = addComment(commentSaveReq);
        } else {
            comment = updateComment(commentSaveReq);
        }
        return comment.getId();
    }

    /**
     * Delete a comment
     *
     * @param commentId The ID of the comment to delete
     * @param userId    The ID of the user deleting the comment
     * @throws Exception if an error occurs during the deletion process
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId, Long userId) {
        CommentDO commentDO = commentDao.getById(commentId);
        if (commentDO == null) {
            throw ExceptionUtil.of(StatusEnum.COMMENT_NOT_EXISTS, "Comment ID=" + commentId);
        }
        if (Objects.equals(commentDO.getUserId(), userId)) {
            throw ExceptionUtil.of(StatusEnum.FORBID_ERROR_MIXED, "You are not authorized to delete this comment");
        }
        ArticleDO article = articleReadService.queryBasicArticle(commentDO.getArticleId());
        if (article == null) {
            throw ExceptionUtil.of(StatusEnum.ARTICLE_NOT_EXISTS, commentDO.getArticleId());
        }
        commentDO.setDeleted(YesOrNoEnum.YES.getCode());
        commentDao.updateById(commentDO);
        userFootWriteService.removeCommentFoot(commentDO, article.getUserId(), getParentCommentUser(commentDO.getParentCommentId()));
        SpringUtil.publishEvent(new NotifyMsgEvent<>(this, NotifyTypeEnum.DELETE_COMMENT, commentDO));
        if (NumUtil.upZero(commentDO.getParentCommentId())) {
            SpringUtil.publishEvent(new NotifyMsgEvent<>(this, NotifyTypeEnum.DELETE_REPLY, commentDO));
        }
    }

    /**
     * Add a new comment
     *
     * @param commentSaveReq The CommentSaveReq object containing the comment information
     * @return The newly added CommentDO object
     */
    private CommentDO addComment(CommentSaveReq commentSaveReq) {
        Long parentCommentUser = getParentCommentUser(commentSaveReq.getParentCommentId());
        CommentDO commentDO = CommentConverter.toDo(commentSaveReq);
        Date now = new Date();
        commentDO.setCreateTime(now);
        commentDO.setUpdateTime(now);
        commentDao.save(commentDO);
        ArticleDO article = articleReadService.queryBasicArticle(commentSaveReq.getArticleId());
        if (article == null) {
            throw ExceptionUtil.of(StatusEnum.ARTICLE_NOT_EXISTS, commentSaveReq.getArticleId());
        }
        userFootWriteService.saveCommentFoot(commentDO, article.getUserId(), parentCommentUser);
        SpringUtil.publishEvent(new NotifyMsgEvent<>(this, NotifyTypeEnum.COMMENT, commentDO));
        if (NumUtil.upZero(parentCommentUser)) {
            SpringUtil.publishEvent(new NotifyMsgEvent<>(this, NotifyTypeEnum.REPLY, commentDO));
        }
        return commentDO;
    }

    /**
     * Update an existing comment
     *
     * @param commentSaveReq The CommentSaveReq object containing the updated comment information
     * @return The updated CommentDO object
     */
    private CommentDO updateComment(CommentSaveReq commentSaveReq) {
        CommentDO commentDO = commentDao.getById(commentSaveReq.getCommentId());
        if (commentDO == null) {
            throw ExceptionUtil.of(StatusEnum.COMMENT_NOT_EXISTS, commentSaveReq.getCommentId());
        }
        commentDO.setContent(commentSaveReq.getCommentContent());
        commentDao.updateById(commentDO);
        return commentDO;
    }

    /**
     * Get the user ID of the parent comment
     *
     * @param parentCommentId The ID of the parent comment
     * @return The user ID of the parent comment
     */
    private Long getParentCommentUser(Long parentCommentId) {
        if (NumUtil.nullOrZero(parentCommentId)) {
            return null;
        }
        CommentDO parent = commentDao.getById(parentCommentId);
        if (parent == null) {
            throw ExceptionUtil.of(StatusEnum.COMMENT_NOT_EXISTS, "Parent Comment ID=" + parentCommentId);
        }
        return parent.getUserId();
    }
}

