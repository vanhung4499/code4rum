package com.hnv99.forum.service.statistics.listener;

import com.hnv99.forum.api.model.enums.ArticleEventEnum;
import com.hnv99.forum.api.model.event.ArticleMsgEvent;
import com.hnv99.forum.api.model.vo.notify.NotifyMsgEvent;
import com.hnv99.forum.core.cache.RedisClient;
import com.hnv99.forum.service.article.repository.dao.ArticleDao;
import com.hnv99.forum.service.article.repository.entity.ArticleDO;
import com.hnv99.forum.service.comment.repository.entity.CommentDO;
import com.hnv99.forum.service.statistics.constants.CountConstants;
import com.hnv99.forum.service.user.repository.entity.UserFootDO;
import com.hnv99.forum.service.user.repository.entity.UserRelationDO;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.hnv99.forum.api.model.enums.NotifyTypeEnum.REPLY;

/**
 * Listener for user activity-related messages
 */
@Component
public class UserStatisticEventListener {
    @Resource
    private ArticleDao articleDao;

    /**
     * Increase corresponding points for user operation behavior
     *
     * @param msgEvent
     */
    @EventListener(classes = NotifyMsgEvent.class)
    @Async
    public void notifyMsgListener(NotifyMsgEvent msgEvent) {
        switch (msgEvent.getNotifyType()) {
            case COMMENT:
            case REPLY:
                CommentDO comment = (CommentDO) msgEvent.getContent();
                RedisClient.hIncr(CountConstants.ARTICLE_STATISTIC_INFO + comment.getArticleId(), CountConstants.COMMENT_COUNT, 1);
                break;
            case DELETE_COMMENT:
            case DELETE_REPLY:
                comment = (CommentDO) msgEvent.getContent();
                RedisClient.hIncr(CountConstants.ARTICLE_STATISTIC_INFO + comment.getArticleId(), CountConstants.COMMENT_COUNT, -1);
                break;
            case COLLECT:
                UserFootDO foot = (UserFootDO) msgEvent.getContent();
                RedisClient.hIncr(CountConstants.USER_STATISTIC_INFO + foot.getDocumentUserId(), CountConstants.COLLECTION_COUNT, 1);
                RedisClient.hIncr(CountConstants.ARTICLE_STATISTIC_INFO + foot.getDocumentId(), CountConstants.COLLECTION_COUNT, 1);
                break;
            case CANCEL_COLLECT:
                foot = (UserFootDO) msgEvent.getContent();
                RedisClient.hIncr(CountConstants.USER_STATISTIC_INFO + foot.getDocumentUserId(), CountConstants.COLLECTION_COUNT, -1);
                RedisClient.hIncr(CountConstants.ARTICLE_STATISTIC_INFO + foot.getDocumentId(), CountConstants.COLLECTION_COUNT, -1);
                break;
            case PRAISE:
                foot = (UserFootDO) msgEvent.getContent();
                RedisClient.hIncr(CountConstants.USER_STATISTIC_INFO + foot.getDocumentUserId(), CountConstants.PRAISE_COUNT, 1);
                RedisClient.hIncr(CountConstants.ARTICLE_STATISTIC_INFO + foot.getDocumentId(), CountConstants.PRAISE_COUNT, 1);
                break;
            case CANCEL_PRAISE:
                foot = (UserFootDO) msgEvent.getContent();
                RedisClient.hIncr(CountConstants.USER_STATISTIC_INFO + foot.getDocumentUserId(), CountConstants.PRAISE_COUNT, -1);
                RedisClient.hIncr(CountConstants.ARTICLE_STATISTIC_INFO + foot.getDocumentId(), CountConstants.PRAISE_COUNT, -1);
                break;
            case FOLLOW:
                UserRelationDO relation = (UserRelationDO) msgEvent.getContent();
                // Increase the number of followers for the main user
                RedisClient.hIncr(CountConstants.USER_STATISTIC_INFO + relation.getUserId(), CountConstants.FANS_COUNT, 1);
                // Increase the number of followers for the fan
                RedisClient.hIncr(CountConstants.USER_STATISTIC_INFO + relation.getFollowUserId(), CountConstants.FOLLOW_COUNT, 1);
                break;
            case CANCEL_FOLLOW:
                relation = (UserRelationDO) msgEvent.getContent();
                // Decrease the number of followers for the main user
                RedisClient.hIncr(CountConstants.USER_STATISTIC_INFO + relation.getUserId(), CountConstants.FANS_COUNT, -1);
                // Decrease the number of followers for the fan
                RedisClient.hIncr(CountConstants.USER_STATISTIC_INFO + relation.getFollowUserId(), CountConstants.FOLLOW_COUNT, -1);
                break;
            default:
        }
    }

    /**
     * Publish article, update corresponding article count
     *
     * @param event
     */
    @Async
    @EventListener(ArticleMsgEvent.class)
    public void publishArticleListener(ArticleMsgEvent<ArticleDO> event) {
        ArticleEventEnum type = event.getType();
        if (type == ArticleEventEnum.ONLINE || type == ArticleEventEnum.OFFLINE || type == ArticleEventEnum.DELETE) {
            Long userId = event.getContent().getUserId();
            int count = articleDao.countArticleByUser(userId);
            RedisClient.hSet(CountConstants.USER_STATISTIC_INFO + userId, CountConstants.ARTICLE_COUNT, count);
        }
    }
}

