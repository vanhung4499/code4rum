package com.hnv99.forum.service.config.service;

import java.util.Map;

/**
 * Service for managing dictionaries.
 */
public interface DictCommonService {

    /**
     * Get dictionary values.
     *
     * @return A map containing the dictionary values.
     */
    Map<String, Object> getDict();
}
