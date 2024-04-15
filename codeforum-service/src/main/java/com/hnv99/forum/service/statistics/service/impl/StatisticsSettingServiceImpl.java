package com.hnv99.forum.service.statistics.service.impl;

import com.hnv99.forum.api.model.vo.statistics.dto.StatisticsCountDTO;
import com.hnv99.forum.api.model.vo.statistics.dto.StatisticsDayDTO;
import com.hnv99.forum.api.model.vo.user.dto.UserFootStatisticDTO;
import com.hnv99.forum.service.article.service.ArticleReadService;
import com.hnv99.forum.service.article.service.ColumnService;
import com.hnv99.forum.service.statistics.repository.entity.RequestCountDO;
import com.hnv99.forum.service.statistics.service.RequestCountService;
import com.hnv99.forum.service.statistics.service.StatisticsSettingService;
import com.hnv99.forum.service.user.service.UserFootService;
import com.hnv99.forum.service.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Backend statistics service implementation
 */
@Slf4j
@Service
public class StatisticsSettingServiceImpl implements StatisticsSettingService {

    @Autowired
    private RequestCountService requestCountService;

    @Autowired
    private UserService userService;

    @Autowired
    private ColumnService columnService;

    @Autowired
    private UserFootService userFootService;

    @Autowired
    private ArticleReadService articleReadService;


    @Override
    public void saveRequestCount(String host) {
        RequestCountDO requestCountDO = requestCountService.getRequestCount(host);
        if (requestCountDO == null) {
            requestCountService.insert(host);
        } else {
            requestCountService.incrementCount(requestCountDO.getId());
        }
    }

    @Override
    public StatisticsCountDTO getStatisticsCount() {
        UserFootStatisticDTO userFootStatisticDTO = userFootService.getFootCount();
        if (userFootStatisticDTO == null) {
            userFootStatisticDTO = new UserFootStatisticDTO();
        }
        return StatisticsCountDTO.builder()
                .userCount(userService.getUserCount())
                .articleCount(articleReadService.getArticleCount())
                .pvCount(requestCountService.getPvTotalCount())
                .tutorialCount(columnService.getTutorialCount())
                .commentCount(userFootStatisticDTO.getCommentCount())
                .collectCount(userFootStatisticDTO.getCollectionCount())
                .likeCount(userFootStatisticDTO.getPraiseCount())
                .readCount(userFootStatisticDTO.getReadCount())
                .build();
    }

    @Override
    public List<StatisticsDayDTO> getPvUvDayList(Integer day) {
        return requestCountService.getPvUvDayList(day);
    }
}

