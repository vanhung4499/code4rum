package com.hnv99.forum.service.statistics.repository.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnv99.forum.api.model.vo.statistics.dto.StatisticsDayDTO;
import com.hnv99.forum.service.statistics.repository.entity.RequestCountDO;
import com.hnv99.forum.service.statistics.repository.mapper.RequestCountMapper;
import org.springframework.stereotype.Repository;
import java.sql.Date;
import java.util.List;

/**
 * Repository for request count data
 */
@Repository
public class RequestCountDao extends ServiceImpl<RequestCountMapper, RequestCountDO> {

    /**
     * Get the total PV count
     *
     * @return the total PV count
     */
    public Long getPvTotalCount() {
        return baseMapper.getPvTotalCount();
    }

    /**
     * Get request count data for a specific host and date
     *
     * @param host the host
     * @param date the date
     * @return the request count data
     */
    public RequestCountDO getRequestCount(String host, Date date) {
        return lambdaQuery()
                .eq(RequestCountDO::getHost, host)
                .eq(RequestCountDO::getDate, date)
                .one();
    }

    /**
     * Get PV UV data list for a specific day
     *
     * @param day the number of days
     * @return the PV UV data list
     */
    public List<StatisticsDayDTO> getPvUvDayList(Integer day) {
        return baseMapper.getPvUvDayList(day);
    }

    /**
     * Increment the count for a specific ID
     *
     * @param id the ID to increment
     */
    public void incrementCount(Long id) {
        baseMapper.incrementCount(id);
    }
}

