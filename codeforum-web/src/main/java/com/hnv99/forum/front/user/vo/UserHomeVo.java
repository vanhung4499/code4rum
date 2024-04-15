package com.hnv99.forum.front.user.vo;

import com.hnv99.forum.api.model.enums.FollowSelectEnum;
import com.hnv99.forum.api.model.vo.PageListVo;
import com.hnv99.forum.api.model.vo.article.dto.ArticleDTO;
import com.hnv99.forum.api.model.vo.article.dto.TagSelectDTO;
import com.hnv99.forum.api.model.vo.user.dto.FollowUserInfoDTO;
import com.hnv99.forum.api.model.vo.user.dto.UserStatisticInfoDTO;
import lombok.Data;

import java.util.List;

/**
 * Represents the user's home view.
 */
@Data
public class UserHomeVo {
    /**
     * Type of selection for the home.
     */
    private String homeSelectType;

    /**
     * List of tags selected for the home.
     */
    private List<TagSelectDTO> homeSelectTags;

    /**
     * List of users the user is following or who are following the user.
     */
    private PageListVo<FollowUserInfoDTO> followList;

    /**
     * Type of selection for the follow list.
     *
     * @see FollowSelectEnum#getCode()
     */
    private String followSelectType;

    /**
     * List of tags selected for the follow list.
     */
    private List<TagSelectDTO> followSelectTags;

    /**
     * Information about the user's home.
     */
    private UserStatisticInfoDTO userHome;

    /**
     * List of articles selected for the home.
     */
    private PageListVo<ArticleDTO> homeSelectList;
}
