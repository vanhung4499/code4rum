package com.hnv99.forum.service.rank.service;

import com.hnv99.forum.api.model.enums.rank.ActivityRankTimeEnum;
import com.hnv99.forum.api.model.vo.rank.dto.RankItemDTO;
import com.hnv99.forum.service.rank.service.model.ActivityScoreBo;

import java.util.List;

/**
 * User Activity Ranking Service Interface
 */
public interface UserActivityRankService {
    /**
     * Add activity score
     *
     * @param userId
     * @param activityScore
     */
    void addActivityScore(Long userId, ActivityScoreBo activityScore);

    /**
     * Query user's activity information
     *
     * @param userId
     * @param time
     * @return
     */
    RankItemDTO queryRankInfo(Long userId, ActivityRankTimeEnum time);

    /**
     * Query activity ranking list
     *
     * @param time
     * @return
     */
    List<RankItemDTO> queryRankList(ActivityRankTimeEnum time, int size);
}
