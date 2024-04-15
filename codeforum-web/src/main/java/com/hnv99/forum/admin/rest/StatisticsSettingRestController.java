package com.hnv99.forum.admin.rest;

import com.hnv99.forum.api.model.vo.ResVo;
import com.hnv99.forum.api.model.vo.statistics.dto.StatisticsCountDTO;
import com.hnv99.forum.api.model.vo.statistics.dto.StatisticsDayDTO;
import com.hnv99.forum.core.permission.Permission;
import com.hnv99.forum.core.permission.UserRole;
import com.hnv99.forum.service.statistics.service.StatisticsSettingService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Backend for data statistics.
 * Manages full-stack statistical analysis.
 *
 * @author LouZai
 * @date 2022/9/19
 */
@RestController
@Permission(role = UserRole.LOGIN)
@Api(value = "Full-stack statistical analysis controller", tags = "Statistics Analysis")
@RequestMapping(path = {"api/admin/statistics/", "admin/statistics/"})
public class StatisticsSettingRestController {

    @Autowired
    private StatisticsSettingService statisticsSettingService;

    static final Integer DEFAULT_DAY = 7;

    /**
     * Query total statistics.
     *
     * @return A response containing total statistics.
     */
    @GetMapping(path = "queryTotal")
    public ResVo<StatisticsCountDTO> queryTotal() {
        StatisticsCountDTO statisticsCountDTO = statisticsSettingService.getStatisticsCount();
        return ResVo.ok(statisticsCountDTO);
    }

    /**
     * Get PV and UV statistics for the specified number of days.
     *
     * @param day The number of days for which to retrieve statistics.
     * @return A response containing PV and UV statistics for the specified days.
     */
    @ResponseBody
    @GetMapping(path = "pvUvDayList")
    public ResVo<List<StatisticsDayDTO>> pvUvDayList(@RequestParam(name = "day", required = false) Integer day) {
        day = (day == null || day == 0) ? DEFAULT_DAY : day;
        List<StatisticsDayDTO> pvDayList = statisticsSettingService.getPvUvDayList(day);
        return ResVo.ok(pvDayList);
    }

}

