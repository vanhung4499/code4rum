package com.hnv99.forum.service.config.service.impl;

import com.hnv99.forum.api.model.event.ConfigRefreshEvent;
import com.hnv99.forum.api.model.exception.ExceptionUtil;
import com.hnv99.forum.api.model.vo.PageVo;
import com.hnv99.forum.api.model.vo.config.GlobalConfigReq;
import com.hnv99.forum.api.model.vo.config.SearchGlobalConfigReq;
import com.hnv99.forum.api.model.vo.config.dto.GlobalConfigDTO;
import com.hnv99.forum.api.model.vo.constants.StatusEnum;
import com.hnv99.forum.core.sensitive.SensitiveProperty;
import com.hnv99.forum.core.sensitive.SensitiveService;
import com.hnv99.forum.core.util.NumUtil;
import com.hnv99.forum.core.util.SpringUtil;
import com.hnv99.forum.service.config.converter.ConfigStructMapper;
import com.hnv99.forum.service.config.repository.dao.ConfigDao;
import com.hnv99.forum.service.config.repository.entity.GlobalConfigDO;
import com.hnv99.forum.service.config.repository.params.SearchGlobalConfigParams;
import com.hnv99.forum.service.config.service.GlobalConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Global configuration service implementation
 */
@Service
public class GlobalConfigServiceImpl implements GlobalConfigService {
    @Autowired
    private ConfigDao configDao;

    @Override
    public PageVo<GlobalConfigDTO> getList(SearchGlobalConfigReq req) {
        ConfigStructMapper mapper = ConfigStructMapper.INSTANCE;
        // Convert
        SearchGlobalConfigParams params = mapper.toSearchGlobalParams(req);
        // Query
        List<GlobalConfigDO> list = configDao.listGlobalConfig(params);
        // Total count
        Long total = configDao.countGlobalConfig(params);

        return PageVo.build(mapper.toGlobalDTOS(list), params.getPageSize(), params.getPageNum(), total);
    }

    @Override
    public void save(GlobalConfigReq req) {
        GlobalConfigDO globalConfigDO = ConfigStructMapper.INSTANCE.toGlobalDO(req);
        // If id is not empty
        if (NumUtil.nullOrZero(globalConfigDO.getId())) {
            configDao.save(globalConfigDO);
        } else {
            configDao.updateById(globalConfigDO);
        }

        // After the configuration is updated, actively trigger dynamic loading of the configuration
        SpringUtil.publishEvent(new ConfigRefreshEvent(this, req.getKeywords(), req.getValue()));
    }

    @Override
    public void delete(Long id) {
        GlobalConfigDO globalConfigDO = configDao.getGlobalConfigById(id);
        if (globalConfigDO != null) {
            configDao.delete(globalConfigDO);
        } else {
            throw ExceptionUtil.of(StatusEnum.RECORDS_NOT_EXISTS, "Record does not exist");
        }
    }

    /**
     * Add sensitive word whitelist
     *
     * @param word
     */
    @Override
    public void addSensitiveWhiteWord(String word) {
        String key = SensitiveProperty.SENSITIVE_KEY_PREFIX + ".allow";
        GlobalConfigReq req = new GlobalConfigReq();
        req.setKeywords(key);

        GlobalConfigDO config = configDao.getGlobalConfigByKey(key);
        if (config == null) {
            req.setValue(word);
            req.setComment("Sensitive Word Whitelist");
        } else {
            req.setValue(config.getValue() + "," + word);
            req.setComment(config.getComment());
            req.setId(config.getId());
        }
        // Update sensitive word whitelist
        save(req);

        // Remove sensitive word record
        SpringUtil.getBean(SensitiveService.class).removeSensitiveWord(word);
    }
}

