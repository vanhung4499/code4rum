package com.hnv99.forum.api.model.vo.rank.dto;

import java.util.List;

import com.hnv99.forum.api.model.enums.rank.ActivityRankTimeEnum;
import lombok.Data;

/**
 * Data transfer object for rank information.
 */
@Data
public class RankInfoDTO {
    // Time period for the rank
    private ActivityRankTimeEnum time;
    // List of items in the rank
    private List<RankItemDTO> items;
}
