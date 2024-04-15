package com.hnv99.forum.service.statistics.service.impl;

import com.hnv99.forum.api.model.vo.statistics.dto.StatisticsDayDTO;
import com.hnv99.forum.service.statistics.repository.dao.RequestCountDao;
import com.hnv99.forum.service.statistics.repository.entity.RequestCountDO;
import com.hnv99.forum.service.statistics.service.RequestCountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of RequestCountService
 */
@Slf4j
@Service
public class RequestCountServiceImpl implements RequestCountService {

    @Autowired
    private RequestCountDao requestCountDao;

    @Override
    public RequestCountDO getRequestCount(String host) {
        return requestCountDao.getRequestCount(host, Date.valueOf(LocalDate.now()));
    }

    @Override
    public void insert(String host) {
        RequestCountDO requestCountDO = null;
        try {
            requestCountDO = new RequestCountDO();
            requestCountDO.setHost(host);
            requestCountDO.setCnt(1);
            requestCountDO.setDate(Date.valueOf(LocalDate.now()));
            requestCountDao.save(requestCountDO);
        } catch (Exception e) {
            // If it's not a database-related exception, it's likely due to concurrent access at midnight,
            // resulting in multiple data entries for the same day; consider using distributed locks to avoid this
            log.error("Error while saving requestCount: {}", requestCountDO, e);
        }
    }

    @Override
    public void incrementCount(Long id) {
        requestCountDao.incrementCount(id);
    }

    @Override
    public Long getPvTotalCount() {
        return requestCountDao.getPvTotalCount();
    }

    @Override
    public List<StatisticsDayDTO> getPvUvDayList(Integer day) {
        return requestCountDao.getPvUvDayList(day);
    }
}
