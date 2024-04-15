package com.hnv99.forum.api.model.vo.config.dto;

import java.io.Serializable;
import lombok.Data;

/**
 * Data transfer object for global configuration.
 */
@Data
public class GlobalConfigDTO implements Serializable {
    // UID
    private static final long serialVersionUID = 1L;

    // ID
    private Long id;
    // Name of the configuration item
    private String keywords;
    // Value of the configuration item
    private String value;
    // Remark or comment
    private String comment;
}
