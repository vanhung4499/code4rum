package com.hnv99.forum.core.sensitive;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Configuration properties for sensitive word filtering, with higher priority given to configuration in the database table, supporting dynamic refreshing.
 */
@Data
@Component
@ConfigurationProperties(prefix = SensitiveProperty.SENSITIVE_KEY_PREFIX)
public class SensitiveProperty {
    public static final String SENSITIVE_KEY_PREFIX = "codeforum.sensitive";

    /**
     * Indicates whether sensitive word filtering is enabled.
     */
    private Boolean enable;

    /**
     * Custom sensitive words.
     */
    private List<String> deny;

    /**
     * Custom non-sensitive words.
     */
    private List<String> allow;
}