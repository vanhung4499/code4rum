package com.hnv99.forum.service.statistics.service;

import com.hnv99.forum.api.model.vo.statistics.dto.StatisticsCountDTO;
import com.hnv99.forum.api.model.vo.statistics.dto.StatisticsDayDTO;

import java.util.List;

/**
 * Backend service interface for data statistics
 */
public interface StatisticsSettingService {

    /**
     * Save request count
     *
     * @param host the host to save count for
     */
    void saveRequestCount(String host);

    /**
     * Get total statistics count
     *
     * @return StatisticsCountDTO containing the total count information
     */
    StatisticsCountDTO getStatisticsCount();

    /**
     * Get PV UV statistics data for each day
     *
     * @param day the number of days to retrieve data for
     * @return List of StatisticsDayDTO containing PV UV statistics data for each day
     */
    List<StatisticsDayDTO> getPvUvDayList(Integer day);
}
