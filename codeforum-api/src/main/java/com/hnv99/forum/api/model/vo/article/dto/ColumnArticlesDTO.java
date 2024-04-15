package com.hnv99.forum.api.model.vo.article.dto;


import com.hnv99.forum.api.model.enums.column.ColumnTypeEnum;
import com.hnv99.forum.api.model.vo.comment.dto.TopCommentDTO;
import lombok.Data;

import java.util.List;

@Data
public class ColumnArticlesDTO {
    /**
     * Column details
     */
    private Long column;

    /**
     * Currently viewed article
     */
    private Integer section;

    /**
     * Article details
     */
    private ArticleDTO article;

    /**
     * 0 Free reading
     * 1 Login required
     * 2 Limited free, if the current time exceeds the limited free period, adjust to login required
     *
     * @see ColumnTypeEnum#getType()
     */
    private Integer readType;

    /**
     * Article comments
     */
    private List<TopCommentDTO> comments;

    /**
     * Hot comments
     */
    private TopCommentDTO hotComment;

    /**
     * Article directory list
     */
    private List<SimpleArticleDTO> articleList;

    // Pagination
    private ArticleOtherDTO other;
}

