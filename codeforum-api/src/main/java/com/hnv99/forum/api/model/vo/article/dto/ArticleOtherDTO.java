package com.hnv99.forum.api.model.vo.article.dto;

import lombok.Data;

@Data
public class ArticleOtherDTO {
    // Article's read type
    private Integer readType;
    // Tutorial's flip
    private ColumnArticleFlipDTO flip;
}
