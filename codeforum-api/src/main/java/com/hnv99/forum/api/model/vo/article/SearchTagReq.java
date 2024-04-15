package com.hnv99.forum.api.model.vo.article;

import lombok.Data;

@Data
public class SearchTagReq {
    // Tag name
    private String tag;
    // Pagination
    private Long pageNumber;
    private Long pageSize;
}

