package com.hnv99.forum.service.config.service.impl;

import com.hnv99.forum.api.model.enums.ConfigTypeEnum;
import com.hnv99.forum.api.model.vo.banner.dto.ConfigDTO;
import com.hnv99.forum.service.config.repository.dao.ConfigDao;
import com.hnv99.forum.service.config.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the service for managing banners on the frontend.
 */
@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private ConfigDao configDao;

    /**
     * Get a list of banners based on the type.
     *
     * @param configTypeEnum The type of banners to retrieve.
     * @return A list of banners.
     */
    @Override
    public List<ConfigDTO> getConfigList(ConfigTypeEnum configTypeEnum) {
        return configDao.listConfigByType(configTypeEnum.getCode());
    }

    /**
     * Update the visit count of a banner after a configuration change.
     * This method is primarily used to invalidate local cache, in coordination with the caching mechanism in SidebarServiceImpl.
     *
     * @param configId The ID of the banner configuration.
     * @param extra    Extra information related to the visit update.
     */
    @Override
    public void updateVisit(long configId, String extra) {
        configDao.updatePdfConfigVisitNum(configId, extra);
    }
}
