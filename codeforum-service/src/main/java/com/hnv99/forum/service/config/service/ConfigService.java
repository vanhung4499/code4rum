package com.hnv99.forum.service.config.service;

import com.hnv99.forum.api.model.enums.ConfigTypeEnum;
import com.hnv99.forum.api.model.vo.banner.dto.ConfigDTO;

import java.util.List;

/**
 * Front-end interface for managing banners.
 */
public interface ConfigService {

    /**
     * Retrieve the list of banners based on the given type.
     *
     * @param configTypeEnum The type of banners to retrieve.
     * @return The list of banners.
     */
    List<ConfigDTO> getConfigList(ConfigTypeEnum configTypeEnum);

    /**
     * Increment the visit count for a specific banner.
     *
     * @param configId The ID of the banner.
     * @param extra    Extra information.
     */
    void updateVisit(long configId, String extra);
}

