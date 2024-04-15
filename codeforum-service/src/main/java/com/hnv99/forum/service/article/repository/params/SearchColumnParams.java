package com.hnv99.forum.service.article.repository.params;

import com.hnv99.forum.api.model.vo.PageParam;
import lombok.Data;

/**
 * Column query
 */
@Data
public class SearchColumnParams extends PageParam {

    /**
     * Column name
     */
    private String column;
}

