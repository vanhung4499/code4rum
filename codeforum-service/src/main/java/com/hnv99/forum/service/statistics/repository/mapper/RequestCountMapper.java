package com.hnv99.forum.service.statistics.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnv99.forum.api.model.vo.statistics.dto.StatisticsDayDTO;
import com.hnv99.forum.service.statistics.repository.entity.RequestCountDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Mapper interface for Request Count
 */
public interface RequestCountMapper extends BaseMapper<RequestCountDO> {

    /**
     * Get the total PV count
     *
     * @return total PV count
     */
    @Select("select sum(cnt) from request_count")
    Long getPvTotalCount();

    /**
     * Get PV UV data list
     *
     * @param day the day
     * @return the list of StatisticsDayDTO
     */
    List<StatisticsDayDTO> getPvUvDayList(@Param("day") Integer day);

    /**
     * Increment count
     *
     * @param id the ID
     */
    @Update("update request_count set cnt = cnt + 1 where id = #{id}")
    void incrementCount(Long id);
}