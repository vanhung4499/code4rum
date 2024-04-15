package com.hnv99.forum.service.config.repository.params;

import com.hnv99.forum.api.model.vo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Search parameters for retrieving global configurations.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SearchGlobalConfigParams extends PageParam {
    /**
     * The name of the configuration item.
     */
    private String key;

    /**
     * The value of the configuration item.
     */
    private String value;

    /**
     * The comment associated with the configuration item.
     */
    private String comment;
}
