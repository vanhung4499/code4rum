package com.hnv99.forum.service.config.repository.params;

import com.hnv99.forum.api.model.vo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Search parameters for retrieving configurations.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SearchConfigParams extends PageParam {
    /**
     * The type of configuration.
     */
    private Integer type;

    /**
     * The name of the configuration.
     */
    private String name;
}
