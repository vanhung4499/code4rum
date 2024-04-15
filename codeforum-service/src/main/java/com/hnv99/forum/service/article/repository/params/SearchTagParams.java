package com.hnv99.forum.service.article.repository.params;

import com.hnv99.forum.api.model.vo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Search tag parameters
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SearchTagParams extends PageParam {
    // Tag name
    private String tag;
}

