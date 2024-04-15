package com.hnv99.forum.api.model.vo.article;

import lombok.Data;

@Data
public class SearchCategoryReq {
    // Category name
    private String category;
    // Pagination
    private Long pageNumber;
    private Long pageSize;
}