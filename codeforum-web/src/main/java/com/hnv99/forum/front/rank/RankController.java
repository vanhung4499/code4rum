package com.hnv99.forum.front.rank;

import com.hnv99.forum.api.model.enums.rank.ActivityRankTimeEnum;
import com.hnv99.forum.api.model.vo.ResVo;
import com.hnv99.forum.api.model.vo.rank.dto.RankInfoDTO;
import com.hnv99.forum.api.model.vo.rank.dto.RankItemDTO;
import com.hnv99.forum.service.rank.service.UserActivityRankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Controller for handling rankings.
 * Supports retrieving the active user ranking list.
 */
@Controller
public class RankController {

    @Autowired
    private UserActivityRankService userActivityRankService;

    /**
     * Retrieves the active user ranking list.
     *
     * @param time  The time period for the ranking list.
     * @param model The model to which the ranking information will be added.
     * @return The view for displaying the ranking list.
     */
    @RequestMapping(path = "/rank/{time}")
    public String rank(@PathVariable(value = "time") String time, Model model) {
        ActivityRankTimeEnum rankTime = ActivityRankTimeEnum.nameOf(time);
        if (rankTime == null) {
            rankTime = ActivityRankTimeEnum.MONTH;
        }
        List<RankItemDTO> list = userActivityRankService.queryRankList(rankTime, 30);
        RankInfoDTO info = new RankInfoDTO();
        info.setItems(list);
        info.setTime(rankTime);
        ResVo<RankInfoDTO> vo = ResVo.ok(info);
        model.addAttribute("vo", vo);
        return "views/rank/index";
    }
}

