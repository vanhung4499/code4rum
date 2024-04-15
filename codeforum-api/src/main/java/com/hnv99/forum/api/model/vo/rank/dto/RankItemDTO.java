package com.hnv99.forum.api.model.vo.rank.dto;

import com.hnv99.forum.api.model.vo.user.dto.SimpleUserInfoDTO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Data transfer object for rank item information.
 */
@Data
@Accessors(chain = true)
public class RankItemDTO {

    /**
     * Rank of the user.
     */
    private Integer rank;

    /**
     * Score of the user.
     */
    private Integer score;

    /**
     * Information of the user.
     */
    private SimpleUserInfoDTO user;
}

