package com.hnv99.forum.front.article.vo;

import com.hnv99.forum.api.model.vo.article.dto.ArticleDTO;
import com.hnv99.forum.api.model.vo.article.dto.ArticleOtherDTO;
import com.hnv99.forum.api.model.vo.comment.dto.TopCommentDTO;
import com.hnv99.forum.api.model.vo.recommend.SideBarDTO;
import com.hnv99.forum.api.model.vo.user.dto.UserStatisticInfoDTO;
import lombok.Data;

import java.util.List;

/**
 * Article Detail View Object
 * Represents the details of an article including article information, comments, hot comments, author information,
 * other information such as pagination and reading type, and sidebar information.
 */
@Data
public class ArticleDetailVo {
    /**
     * Article information
     */
    private ArticleDTO article;

    /**
     * Comments information
     */
    private List<TopCommentDTO> comments;

    /**
     * Hot comment
     */
    private TopCommentDTO hotComment;

    /**
     * Author-related information
     */
    private UserStatisticInfoDTO author;

    /**
     * Other information such as pagination and reading type
     */
    private ArticleOtherDTO other;

    /**
     * Sidebar information
     */
    private List<SideBarDTO> sideBarItems;
}

