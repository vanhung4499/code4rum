package com.hnv99.forum.service.statistics.service.impl;

import com.hnv99.forum.api.model.vo.user.dto.ArticleFootCountDTO;
import com.hnv99.forum.api.model.vo.user.dto.UserStatisticInfoDTO;
import com.hnv99.forum.core.cache.RedisClient;
import com.hnv99.forum.core.util.MapUtils;
import com.hnv99.forum.service.article.repository.dao.ArticleDao;
import com.hnv99.forum.service.comment.service.CommentReadService;
import com.hnv99.forum.service.statistics.constants.CountConstants;
import com.hnv99.forum.service.statistics.service.CountService;
import com.hnv99.forum.service.user.repository.dao.UserDao;
import com.hnv99.forum.service.user.repository.dao.UserFootDao;
import com.hnv99.forum.service.user.repository.dao.UserRelationDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Count service, subsequent counting-related tasks can be considered based on Redis
 */
@Slf4j
@Service
public class CountServiceImpl implements CountService {
    private final UserFootDao userFootDao;

    @Resource
    private UserRelationDao userRelationDao;

    @Resource
    private ArticleDao articleDao;

    @Resource
    private CommentReadService commentReadService;

    @Resource
    private UserDao userDao;

    public CountServiceImpl(UserFootDao userFootDao) {
        this.userFootDao = userFootDao;
    }

    @Override
    public ArticleFootCountDTO queryArticleCountInfoByArticleId(Long articleId) {
        ArticleFootCountDTO res = userFootDao.countArticleByArticleId(articleId);
        if (res == null) {
            res = new ArticleFootCountDTO();
        } else {
            res.setCommentCount(commentReadService.queryCommentCount(articleId));
        }
        return res;
    }


    @Override
    public ArticleFootCountDTO queryArticleCountInfoByUserId(Long userId) {
        return userFootDao.countArticleByUserId(userId);
    }

    /**
     * Query the number of likes for a comment
     *
     * @param commentId the comment ID
     * @return the number of likes for the comment
     */
    @Override
    public Long queryCommentPraiseCount(Long commentId) {
        return userFootDao.countCommentPraise(commentId);
    }

    @Override
    public UserStatisticInfoDTO queryUserStatisticInfo(Long userId) {
        Map<String, Integer> ans = RedisClient.hGetAll(CountConstants.USER_STATISTIC_INFO + userId, Integer.class);
        UserStatisticInfoDTO info = new UserStatisticInfoDTO();
        info.setFollowCount(ans.getOrDefault(CountConstants.FOLLOW_COUNT, 0));
        info.setArticleCount(ans.getOrDefault(CountConstants.ARTICLE_COUNT, 0));
        info.setPraiseCount(ans.getOrDefault(CountConstants.PRAISE_COUNT, 0));
        info.setCollectionCount(ans.getOrDefault(CountConstants.COLLECTION_COUNT, 0));
        info.setReadCount(ans.getOrDefault(CountConstants.READ_COUNT, 0));
        info.setFansCount(ans.getOrDefault(CountConstants.FANS_COUNT, 0));
        return info;
    }

    @Override
    public ArticleFootCountDTO queryArticleStatisticInfo(Long articleId) {
        Map<String, Integer> ans = RedisClient.hGetAll(CountConstants.ARTICLE_STATISTIC_INFO + articleId, Integer.class);
        ArticleFootCountDTO info = new ArticleFootCountDTO();
        info.setPraiseCount(ans.getOrDefault(CountConstants.PRAISE_COUNT, 0));
        info.setCollectionCount(ans.getOrDefault(CountConstants.COLLECTION_COUNT, 0));
        info.setCommentCount(ans.getOrDefault(CountConstants.COMMENT_COUNT, 0));
        info.setReadCount(ans.getOrDefault(CountConstants.READ_COUNT, 0));
        return info;
    }

    @Override
    public void incrArticleReadCount(Long authorUserId, Long articleId) {
        // Increment count in database
        articleDao.incReadCount(articleId);
        // Increment count in Redis
        RedisClient.pipelineAction()
                .add(CountConstants.ARTICLE_STATISTIC_INFO + articleId, CountConstants.READ_COUNT,
                        (connection, key, value) -> connection.hIncrBy(key, value, 1))
                .add(CountConstants.USER_STATISTIC_INFO + authorUserId, CountConstants.READ_COUNT,
                        (connection, key, value) -> connection.hIncrBy(key, value, 1))
                .execute();
    }

    /**
     * Perform scheduled task at 4:15 AM every day to refresh all user's statistical information
     */
    @Scheduled(cron = "0 15 4 * * ?")
    public void autoRefreshAllUserStatisticInfo() {
        Long now = System.currentTimeMillis();
        log.info("Start auto refreshing user statistics information");
        Long userId = 0L;
        int batchSize = 20;
        while (true) {
            List<Long> userIds = userDao.scanUserId(userId, batchSize);
            userIds.forEach(this::refreshUserStatisticInfo);
            if (userIds.size() < batchSize) {
                userId = userIds.get(userIds.size() - 1);
                break;
            } else {
                userId = userIds.get(batchSize - 1);
            }
        }
        log.info("End auto refreshing user statistics information, total time: {}ms, maxUserId: {}", System.currentTimeMillis() - now, userId);
    }


    /**
     * Refresh user's statistical information
     *
     * @param userId the user ID
     */
    public void refreshUserStatisticInfo(Long userId) {
        // Count of user's article likes, collections, and read count
        ArticleFootCountDTO count = userFootDao.countArticleByUserId(userId);
        if (count == null) {
            count = new ArticleFootCountDTO();
        }

        // Get follow count
        Long followCount = userRelationDao.queryUserFollowCount(userId);
        // Get fans count
        Long fansCount = userRelationDao.queryUserFansCount(userId);

        // Query the number of articles published by the user
        Integer articleNum = articleDao.countArticleByUser(userId);

        String key = CountConstants.USER_STATISTIC_INFO + userId;
        RedisClient.hMSet(key, MapUtils.create(CountConstants.PRAISE_COUNT, count.getPraiseCount(),
                CountConstants.COLLECTION_COUNT, count.getCollectionCount(),
                CountConstants.READ_COUNT, count.getReadCount(),
                CountConstants.FANS_COUNT, fansCount,
                CountConstants.FOLLOW_COUNT, followCount,
                CountConstants.ARTICLE_COUNT, articleNum));
    }

    /**
     * Refresh article's statistical information
     *
     * @param articleId the article ID
     */
    public void refreshArticleStatisticInfo(Long articleId) {
        ArticleFootCountDTO res = userFootDao.countArticleByArticleId(articleId);
        if (res == null) {
            res = new ArticleFootCountDTO();
        } else {
            res.setCommentCount(commentReadService.queryCommentCount(articleId));
        }

        RedisClient.hMSet(CountConstants.ARTICLE_STATISTIC_INFO + articleId,
                MapUtils.create(CountConstants.COLLECTION_COUNT, res.getCollectionCount(),
                        CountConstants.PRAISE_COUNT, res.getPraiseCount(),
                        CountConstants.READ_COUNT, res.getReadCount(),
                        CountConstants.COMMENT_COUNT, res.getCommentCount()
                )
        );
    }
}