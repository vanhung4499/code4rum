package com.hnv99.forum.service.rank.service.impl;


import com.hnv99.forum.api.model.enums.rank.ActivityRankTimeEnum;
import com.hnv99.forum.api.model.vo.rank.dto.RankItemDTO;
import com.hnv99.forum.api.model.vo.user.dto.SimpleUserInfoDTO;
import com.hnv99.forum.core.cache.RedisClient;
import com.hnv99.forum.core.util.DateUtil;
import com.hnv99.forum.core.util.NumUtil;
import com.hnv99.forum.service.rank.service.UserActivityRankService;
import com.hnv99.forum.service.rank.service.model.ActivityScoreBo;
import com.hnv99.forum.service.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class UserActivityRankServiceImpl implements UserActivityRankService {
    private static final String ACTIVITY_SCORE_KEY = "activity_rank_";

    @Autowired
    private UserService userService;

    /**
     * Today's activity ranking
     *
     * @return Key of today's ranking
     */
    private String todayRankKey() {
        return ACTIVITY_SCORE_KEY + DateUtil.format(DateTimeFormatter.ofPattern("yyyyMMdd"), System.currentTimeMillis());
    }

    /**
     * Monthly ranking
     *
     * @return Key of monthly ranking
     */
    private String monthRankKey() {
        return ACTIVITY_SCORE_KEY + DateUtil.format(DateTimeFormatter.ofPattern("yyyyMM"), System.currentTimeMillis());
    }

    /**
     * Add activity score
     *
     * @param userId
     * @param activityScore
     */
    @Override
    public void addActivityScore(Long userId, ActivityScoreBo activityScore) {
        if (userId == null) {
            return;
        }

        // 1. Calculate activity (positive for adding activity, negative for subtracting activity)
        String field;
        int score = 0;
        if (activityScore.getPath() != null) {
            field = "path_" + activityScore.getPath();
            score = 1;
        } else if (activityScore.getArticleId() != null) {
            field = activityScore.getArticleId() + "_";
            if (activityScore.getPraise() != null) {
                field += "praise";
                score = BooleanUtils.isTrue(activityScore.getPraise()) ? 2 : -2;
            } else if (activityScore.getCollect() != null) {
                field += "collect";
                score = BooleanUtils.isTrue(activityScore.getCollect()) ? 2 : -2;
            } else if (activityScore.getRate() != null) {
                // Comment reply
                field += "rate";
                score = BooleanUtils.isTrue(activityScore.getRate()) ? 3 : -3;
            } else if (BooleanUtils.isTrue(activityScore.getPublishArticle())) {
                // Publish article
                field += "publish";
                score += 10;
            }
        } else if (activityScore.getFollowedUserId() != null) {
            field = activityScore.getFollowedUserId() + "_follow";
            score = BooleanUtils.isTrue(activityScore.getFollow()) ? 2 : -2;
        } else {
            return;
        }

        final String todayRankKey = todayRankKey();
        final String monthRankKey = monthRankKey();
        // 2. Idempotent: Determine if relevant activity information has been updated before
        final String userActionKey = ACTIVITY_SCORE_KEY + userId + DateUtil.format(DateTimeFormatter.ofPattern("yyyyMMdd"), System.currentTimeMillis());
        Integer ans = RedisClient.hGet(userActionKey, field, Integer.class);
        if (ans == null) {
            // 2.1 There is no record of adding points before, execute specific addition
            if (score > 0) {
                // Record adding points
                RedisClient.hSet(userActionKey, field, score);
                // Personal user operation record, valid for one month, convenient for users to query their recent 31 days of activity
                RedisClient.expire(userActionKey, 31 * DateUtil.ONE_DAY_SECONDS);

                // Update today's and this month's activity rankings
                Double newAns = RedisClient.zIncrBy(todayRankKey, String.valueOf(userId), score);
                RedisClient.zIncrBy(monthRankKey, String.valueOf(userId), score);
                if (log.isDebugEnabled()) {
                    log.info("Activity score update! key#field = {}#{}, add = {}, newScore = {}", todayRankKey, userId, score, newAns);
                }
                if (newAns <= score) {
                    // Since only daily/monthly activity increases are implemented above, but no corresponding expiration period is set; in order to avoid high redis usage caused by persistent storage, here the expiration period is set
                    // Daily activity rankings, save for 31 days; Monthly activity rankings, save for 1 year
                    // Why is it newAns <= score to set the expiration period?
                    // Because newAns is the user's activity of the day, if it is found equal to the activity to be added score, it means that it is the first time to add a record today, so setting the expiration period at this time is more in line with expectations
                    // But please note that there are two defects in the implementation below:
                    //  1. For the validity period of the month, it becomes this month, and the expiration period is recalculated every time the activity score is added for the first time each day, which is inconsistent with the setting of the expiration period when the cache is first added
                    //  2. If you first add 1 activity score, then subtract 1, and then add 1 activity score again, it will also cause the expiration period to be recalculated
                    // A more rigorous approach would be to first judge the ttl of the key, and set the expiration period only for those that have not been set, as follows
                    Long ttl = RedisClient.ttl(todayRankKey);
                    if (!NumUtil.upZero(ttl)) {
                        RedisClient.expire(todayRankKey, 31 * DateUtil.ONE_DAY_SECONDS);
                    }
                    ttl = RedisClient.ttl(monthRankKey);
                    if (!NumUtil.upZero(ttl)) {
                        RedisClient.expire(monthRankKey, 12 * DateUtil.ONE_MONTH_SECONDS);
                    }
                }
            }
        } else if (ans > 0) {
            // 2.2 Points have been added before, so this time subtracting points can be executed
            if (score < 0) {
                // Remove the user's activity execution record --> that is, remove the idempotent key used to prevent duplicate addition of activity score
                Boolean oldHave = RedisClient.hDel(userActionKey, field);
                if (BooleanUtils.isTrue(oldHave)) {
                    Double newAns = RedisClient.zIncrBy(todayRankKey, String.valueOf(userId), score);
                    RedisClient.zIncrBy(monthRankKey, String.valueOf(userId), score);
                    if (log.isDebugEnabled()) {
                        log.info("Activity score update deduction! key#field = {}#{}, add = {}, newScore = {}", todayRankKey, userId, score, newAns);
                    }
                }
            }
        }
    }

    @Override
    public RankItemDTO queryRankInfo(Long userId, ActivityRankTimeEnum time) {
        RankItemDTO item = new RankItemDTO();
        item.setUser(userService.querySimpleUserInfo(userId));

        String rankKey = time == ActivityRankTimeEnum.DAY ? todayRankKey() : monthRankKey();
        ImmutablePair<Integer, Double> rank = RedisClient.zRankInfo(rankKey, String.valueOf(userId));
        item.setRank(rank.getLeft());
        item.setScore(rank.getRight().intValue());
        return item;
    }

    @Override
    public List<RankItemDTO> queryRankList(ActivityRankTimeEnum time, int size) {
        String rankKey = time == ActivityRankTimeEnum.DAY ? todayRankKey() : monthRankKey();
        // 1. Get the top N active users
        List<ImmutablePair<String, Double>> rankList = RedisClient.zTopNScore(rankKey, size);
        if (CollectionUtils.isEmpty(rankList)) {
            return Collections.emptyList();
        }

        // 2. Query basic information of users
        // Build userId -> active score map mapping for supplementing user information
        Map<Long, Integer> userScoreMap = rankList.stream().collect(Collectors.toMap(s -> Long.valueOf(s.getLeft()), s -> s.getRight().intValue()));
        List<SimpleUserInfoDTO> users = userService.batchQuerySimpleUserInfo(userScoreMap.keySet());

        // 3. Sort by score
        List<RankItemDTO> rank = users.stream()
                .map(user -> new RankItemDTO().setUser(user).setScore(userScoreMap.getOrDefault(user.getUserId(), 0)))
                .sorted((o1, o2) -> Integer.compare(o2.getScore(), o1.getScore()))
                .collect(Collectors.toList());

        // 4. Supplement the ranking of each user
        IntStream.range(0, rank.size()).forEach(i -> rank.get(i).setRank(i + 1));
        return rank;
    }
}

