package com.hnv99.forum.service.rank.service.listener;

import com.hnv99.forum.api.model.context.ReqInfoContext;
import com.hnv99.forum.api.model.enums.ArticleEventEnum;
import com.hnv99.forum.api.model.event.ArticleMsgEvent;
import com.hnv99.forum.api.model.vo.notify.NotifyMsgEvent;
import com.hnv99.forum.service.article.repository.entity.ArticleDO;
import com.hnv99.forum.service.comment.repository.entity.CommentDO;
import com.hnv99.forum.service.rank.service.UserActivityRankService;
import com.hnv99.forum.service.rank.service.model.ActivityScoreBo;
import com.hnv99.forum.service.user.repository.entity.UserFootDO;
import com.hnv99.forum.service.user.repository.entity.UserRelationDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * User Activity Related Message Listener
 *
 * Listens to user activity events and updates corresponding scores.
 */
@Component
public class UserActivityListener {
    @Autowired
    private UserActivityRankService userActivityRankService;

    /**
     * Increase corresponding points for user actions
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
                userActivityRankService.addActivityScore(ReqInfoContext.getReqInfo().getUserId(), new ActivityScoreBo().setRate(true).setArticleId(comment.getArticleId()));
                break;
            case COLLECT:
                UserFootDO foot = (UserFootDO) msgEvent.getContent();
                userActivityRankService.addActivityScore(ReqInfoContext.getReqInfo().getUserId(), new ActivityScoreBo().setCollect(true).setArticleId(foot.getDocumentId()));
                break;
            case CANCEL_COLLECT:
                foot = (UserFootDO) msgEvent.getContent();
                userActivityRankService.addActivityScore(ReqInfoContext.getReqInfo().getUserId(), new ActivityScoreBo().setCollect(false).setArticleId(foot.getDocumentId()));
                break;
            case PRAISE:
                foot = (UserFootDO) msgEvent.getContent();
                userActivityRankService.addActivityScore(ReqInfoContext.getReqInfo().getUserId(), new ActivityScoreBo().setPraise(true).setArticleId(foot.getDocumentId()));
                break;
            case CANCEL_PRAISE:
                foot = (UserFootDO) msgEvent.getContent();
                userActivityRankService.addActivityScore(ReqInfoContext.getReqInfo().getUserId(), new ActivityScoreBo().setPraise(false).setArticleId(foot.getDocumentId()));
                break;
            case FOLLOW:
                UserRelationDO relation = (UserRelationDO) msgEvent.getContent();
                userActivityRankService.addActivityScore(ReqInfoContext.getReqInfo().getUserId(), new ActivityScoreBo().setFollow(true).setArticleId(relation.getUserId()));
                break;
            case CANCEL_FOLLOW:
                relation = (UserRelationDO) msgEvent.getContent();
                userActivityRankService.addActivityScore(ReqInfoContext.getReqInfo().getUserId(), new ActivityScoreBo().setFollow(false).setArticleId(relation.getUserId()));
                break;
            default:
        }
    }

    /**
     * Update corresponding scores when publishing an article
     *
     * @param event
     */
    @Async
    @EventListener(ArticleMsgEvent.class)
    public void publishArticleListener(ArticleMsgEvent<ArticleDO> event) {
        ArticleEventEnum type = event.getType();
        if (type == ArticleEventEnum.ONLINE) {
            userActivityRankService.addActivityScore(ReqInfoContext.getReqInfo().getUserId(), new ActivityScoreBo().setPublishArticle(true).setArticleId(event.getContent().getId()));
        }
    }

}

