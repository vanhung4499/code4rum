package com.hnv99.forum.service.statistics.service;

import com.hnv99.forum.api.model.vo.statistics.dto.StatisticsDayDTO;
import com.hnv99.forum.service.statistics.repository.entity.RequestCountDO;

import java.util.List;

public interface RequestCountService {
    RequestCountDO getRequestCount(String host);

    void insert(String host);

    void incrementCount(Long id);

    Long getPvTotalCount();

    List<StatisticsDayDTO> getPvUvDayList(Integer day);
}
