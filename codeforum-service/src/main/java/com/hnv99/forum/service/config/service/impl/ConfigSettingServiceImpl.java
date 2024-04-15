package com.hnv99.forum.service.config.service.impl;

import com.hnv99.forum.api.model.enums.YesOrNoEnum;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.PageVo;
import com.hnv99.forum.api.model.vo.banner.ConfigReq;
import com.hnv99.forum.api.model.vo.banner.SearchConfigReq;
import com.hnv99.forum.api.model.vo.banner.dto.ConfigDTO;
import com.hnv99.forum.core.util.NumUtil;
import com.hnv99.forum.service.config.converter.ConfigStructMapper;
import com.hnv99.forum.service.config.repository.dao.ConfigDao;
import com.hnv99.forum.service.config.repository.entity.ConfigDO;
import com.hnv99.forum.service.config.repository.params.SearchConfigParams;
import com.hnv99.forum.service.config.service.ConfigSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Banner backend interface
 */
@Service
public class ConfigSettingServiceImpl implements ConfigSettingService {

    @Autowired
    private ConfigDao configDao;

    @Override
    public void saveConfig(ConfigReq configReq) {
        ConfigDO configDO = ConfigStructMapper.INSTANCE.toDO(configReq);
        if (NumUtil.nullOrZero(configReq.getConfigId())) {
            configDao.save(configDO);
        } else {
            configDO.setId(configReq.getConfigId());
            configDao.updateById(configDO);
        }
    }

    @Override
    public void deleteConfig(Integer configId) {
        ConfigDO configDO = configDao.getById(configId);
        if (configDO != null){
            configDO.setDeleted(YesOrNoEnum.YES.getCode());
            configDao.updateById(configDO);
        }
    }

    @Override
    public void operateConfig(Integer configId, Integer pushStatus) {
        ConfigDO configDO = configDao.getById(configId);
        if (configDO != null){
            configDO.setStatus(pushStatus);
            configDao.updateById(configDO);
        }
    }

    @Override
    public PageVo<ConfigDTO> getConfigList(SearchConfigReq req) {
        // Convert
        SearchConfigParams params = ConfigStructMapper.INSTANCE.toSearchParams(req);
        // Query
        List<ConfigDTO> configDTOs = configDao.listBanner(params);
        Long totalCount = configDao.countConfig(params);
        return PageVo.build(configDTOs, params.getPageSize(), params.getPageNum(), totalCount);
    }

    @Override
    public PageVo<ConfigDTO> getNoticeList(PageParam pageParam) {
        List<ConfigDTO> configDTOS = configDao.listNotice(pageParam);
        Integer totalCount = configDao.countNotice();
        return PageVo.build(configDTOS, pageParam.getPageSize(), pageParam.getPageNum(), totalCount);
    }
}

