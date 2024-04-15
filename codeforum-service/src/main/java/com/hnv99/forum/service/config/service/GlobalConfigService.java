package com.hnv99.forum.service.config.service;

import com.hnv99.forum.api.model.vo.PageVo;
import com.hnv99.forum.api.model.vo.config.GlobalConfigReq;
import com.hnv99.forum.api.model.vo.config.SearchGlobalConfigReq;
import com.hnv99.forum.api.model.vo.config.dto.GlobalConfigDTO;

/**
 * Service for managing global configurations.
 */
public interface GlobalConfigService {
    /**
     * Get a list of global configurations based on search criteria.
     *
     * @param req The search criteria.
     * @return A page containing the list of global configurations.
     */
    PageVo<GlobalConfigDTO> getList(SearchGlobalConfigReq req);

    /**
     * Save a global configuration.
     *
     * @param req The global configuration to be saved.
     */
    void save(GlobalConfigReq req);

    /**
     * Delete a global configuration by its ID.
     *
     * @param id The ID of the global configuration to be deleted.
     */
    void delete(Long id);

    /**
     * Add a sensitive word to the whitelist.
     *
     * @param word The sensitive word to be added to the whitelist.
     */
    void addSensitiveWhiteWord(String word);
}
