package com.hnv99.forum.service.notify.service.impl;

import com.hnv99.forum.api.model.enums.NotifyStatEnum;
import com.hnv99.forum.api.model.enums.NotifyTypeEnum;
import com.hnv99.forum.api.model.vo.notify.NotifyMsgEvent;
import com.hnv99.forum.core.util.SpringUtil;
import com.hnv99.forum.service.article.repository.entity.ArticleDO;
import com.hnv99.forum.service.article.service.ArticleReadService;
import com.hnv99.forum.service.comment.repository.entity.CommentDO;
import com.hnv99.forum.service.comment.service.CommentReadService;
import com.hnv99.forum.service.notify.repository.dao.NotifyMsgDao;
import com.hnv99.forum.service.notify.repository.entity.NotifyMsgDO;
import com.hnv99.forum.service.user.repository.entity.UserFootDO;
import com.hnv99.forum.service.user.repository.entity.UserRelationDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Listener for handling notification messages asynchronously.
 */
@Slf4j
@Async
@Service
public class NotifyMsgListener<T> implements ApplicationListener<NotifyMsgEvent<T>> {
    private static final Long ADMIN_ID = 1L;
    private final ArticleReadService articleReadService;
    private final CommentReadService commentReadService;
    private final NotifyMsgDao notifyMsgDao;

    public NotifyMsgListener(ArticleReadService articleReadService,
                             CommentReadService commentReadService,
                             NotifyMsgDao notifyMsgDao) {
        this.articleReadService = articleReadService;
        this.commentReadService = commentReadService;
        this.notifyMsgDao = notifyMsgDao;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onApplicationEvent(NotifyMsgEvent<T> msgEvent) {
        switch (msgEvent.getNotifyType()) {
            case COMMENT:
                saveCommentNotify((NotifyMsgEvent<CommentDO>) msgEvent);
                break;
            case REPLY:
                saveReplyNotify((NotifyMsgEvent<CommentDO>) msgEvent);
                break;
            case PRAISE:
            case COLLECT:
                saveArticleNotify((NotifyMsgEvent<UserFootDO>) msgEvent);
                break;
            case CANCEL_PRAISE:
            case CANCEL_COLLECT:
                removeArticleNotify((NotifyMsgEvent<UserFootDO>) msgEvent);
                break;
            case FOLLOW:
                saveFollowNotify((NotifyMsgEvent<UserRelationDO>) msgEvent);
                break;
            case CANCEL_FOLLOW:
                removeFollowNotify((NotifyMsgEvent<UserRelationDO>) msgEvent);
                break;
            case LOGIN:
                // todo Handle user login, determine whether to insert new notification messages, currently not implemented
                break;
            case REGISTER:
                // For first-time registration, insert a welcome message
                saveRegisterSystemNotify((Long) msgEvent.getContent());
                break;
            default:
                // todo Handle system messages
        }
    }

    /**
     * Save notification for comments and replies.
     *
     * @param event the notification event
     */
    private void saveCommentNotify(NotifyMsgEvent<CommentDO> event) {
        NotifyMsgDO msg = new NotifyMsgDO();
        CommentDO comment = event.getContent();
        ArticleDO article = articleReadService.queryBasicArticle(comment.getArticleId());
        msg.setNotifyUserId(article.getUserId())
                .setOperateUserId(comment.getUserId())
                .setRelatedId(article.getId())
                .setType(event.getNotifyType().getType())
                .setState(NotifyStatEnum.UNREAD.getStat())
                .setMsg(comment.getContent());
        // For comments, support multiple comments; so previous ones are not deleted
        notifyMsgDao.save(msg);
    }

    /**
     * Save notification for comment replies.
     *
     * @param event the notification event
     */
    private void saveReplyNotify(NotifyMsgEvent<CommentDO> event) {
        NotifyMsgDO msg = new NotifyMsgDO();
        CommentDO comment = event.getContent();
        CommentDO parent = commentReadService.queryComment(comment.getParentCommentId());
        msg.setNotifyUserId(parent.getUserId())
                .setOperateUserId(comment.getUserId())
                .setRelatedId(comment.getArticleId())
                .setType(event.getNotifyType().getType())
                .setState(NotifyStatEnum.UNREAD.getStat())
                .setMsg(comment.getContent());
        // Replies also support multiple replies; no idempotent check is performed
        notifyMsgDao.save(msg);
    }

    /**
     * Save notification for likes and favorites.
     *
     * @param event the notification event
     */
    private void saveArticleNotify(NotifyMsgEvent<UserFootDO> event) {
        UserFootDO foot = event.getContent();
        NotifyMsgDO msg = new NotifyMsgDO().setRelatedId(foot.getDocumentId())
                .setNotifyUserId(foot.getDocumentUserId())
                .setOperateUserId(foot.getUserId())
                .setType(event.getNotifyType().getType())
                .setState(NotifyStatEnum.UNREAD.getStat())
                .setMsg("");
        NotifyMsgDO record = notifyMsgDao.getByUserIdRelatedIdAndType(msg);
        if (record == null) {
            // If there's no corresponding notification before, save it; because a user can like or unlike an article multiple times,
            // but we only want to notify once
            notifyMsgDao.save(msg);
        }
    }

    /**
     * Remove notification for unlikes and unfavorites.
     *
     * @param event the notification event
     */
    private void removeArticleNotify(NotifyMsgEvent<UserFootDO> event) {
        UserFootDO foot = event.getContent();
        NotifyMsgDO msg = new NotifyMsgDO()
                .setRelatedId(foot.getDocumentId())
                .setNotifyUserId(foot.getDocumentUserId())
                .setOperateUserId(foot.getUserId())
                .setType(event.getNotifyType().getType())
                .setMsg("");
        NotifyMsgDO record = notifyMsgDao.getByUserIdRelatedIdAndType(msg);
        if (record != null) {
            notifyMsgDao.removeById(record.getId());
        }
    }

    /**
     * Save notification for follows.
     *
     * @param event the notification event
     */
    private void saveFollowNotify(NotifyMsgEvent<UserRelationDO> event) {
        UserRelationDO relation = event.getContent();
        NotifyMsgDO msg = new NotifyMsgDO().setRelatedId(0L)
                .setNotifyUserId(relation.getUserId())
                .setOperateUserId(relation.getFollowUserId())
                .setType(event.getNotifyType().getType())
                .setState(NotifyStatEnum.UNREAD.getStat())
                .setMsg("");
        NotifyMsgDO record = notifyMsgDao.getByUserIdRelatedIdAndType(msg);
        if (record == null) {
            // If there's no corresponding notification before, save it; because a user's follow relationship is one-to-one,
            // they can follow and unfollow multiple times, but we only want to notify once
            notifyMsgDao.save(msg);
        }
    }

    /**
     * Remove notification for unfollows.
     *
     * @param event the notification event
     */
    private void removeFollowNotify(NotifyMsgEvent<UserRelationDO> event) {
        UserRelationDO relation = event.getContent();
        NotifyMsgDO msg = new NotifyMsgDO()
                .setRelatedId(0L)
                .setNotifyUserId(relation.getUserId())
                .setOperateUserId(relation.getFollowUserId())
                .setType(event.getNotifyType().getType())
                .setMsg("");
        NotifyMsgDO record = notifyMsgDao.getByUserIdRelatedIdAndType(msg);
        if (record != null) {
            notifyMsgDao.removeById(record.getId());
        }
    }

    /**
     * Save notification for user registration.
     *
     * @param userId the ID of the registered user
     */
    private void saveRegisterSystemNotify(Long userId) {
        NotifyMsgDO msg = new NotifyMsgDO().setRelatedId(0L)
                .setNotifyUserId(userId)
                .setOperateUserId(ADMIN_ID)
                .setType(NotifyTypeEnum.REGISTER.getType())
                .setState(NotifyStatEnum.UNREAD.getStat())
                .setMsg(SpringUtil.getConfig("view.site.welcomeInfo"));
        NotifyMsgDO record = notifyMsgDao.getByUserIdRelatedIdAndType(msg);
        if (record == null) {
            // If there's no corresponding notification before, save it; because user registration is one-to-one,
            // they can register multiple times, but we only want to notify once
            notifyMsgDao.save(msg);
        }
    }
}
