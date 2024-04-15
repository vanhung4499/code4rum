package com.hnv99.forum.service.config.service;

import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.PageVo;
import com.hnv99.forum.api.model.vo.banner.ConfigReq;
import com.hnv99.forum.api.model.vo.banner.SearchConfigReq;
import com.hnv99.forum.api.model.vo.banner.dto.ConfigDTO;

import java.util.List;

/**
 * Backend interface for managing banners.
 */
public interface ConfigSettingService {

    /**
     * Save a banner configuration.
     *
     * @param configReq The request containing the banner configuration details.
     */
    void saveConfig(ConfigReq configReq);

    /**
     * Delete a banner configuration by ID.
     *
     * @param bannerId The ID of the banner to delete.
     */
    void deleteConfig(Integer bannerId);

    /**
     * Perform an operation on a banner (e.g., publish or unpublish).
     *
     * @param bannerId    The ID of the banner to operate on.
     * @param pushStatus  The status of the operation (e.g., publish or unpublish).
     */
    void operateConfig(Integer bannerId, Integer pushStatus);

    /**
     * Get a list of banners based on the provided search parameters.
     *
     * @param params The search parameters.
     * @return A page containing the list of banners.
     */
    PageVo<ConfigDTO> getConfigList(SearchConfigReq params);

    /**
     * Get a list of announcements.
     *
     * @param pageParam The pagination parameters.
     * @return A page containing the list of announcements.
     */
    PageVo<ConfigDTO> getNoticeList(PageParam pageParam);
}
