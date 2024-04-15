package com.hnv99.forum.service.config.repository.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnv99.forum.api.model.enums.ConfigTypeEnum;
import com.hnv99.forum.api.model.enums.PushStatusEnum;
import com.hnv99.forum.api.model.enums.YesOrNoEnum;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.banner.dto.ConfigDTO;
import com.hnv99.forum.service.config.converter.ConfigConverter;
import com.hnv99.forum.service.config.converter.ConfigStructMapper;
import com.hnv99.forum.service.config.repository.entity.ConfigDO;
import com.hnv99.forum.service.config.repository.entity.GlobalConfigDO;
import com.hnv99.forum.service.config.repository.mapper.ConfigMapper;
import com.hnv99.forum.service.config.repository.mapper.GlobalConfigMapper;
import com.hnv99.forum.service.config.repository.params.SearchConfigParams;
import com.hnv99.forum.service.config.repository.params.SearchGlobalConfigParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Date;

@Repository
public class ConfigDao extends ServiceImpl<ConfigMapper, ConfigDO> {
    @Resource
    private GlobalConfigMapper globalConfigMapper;

    /**
     * Get the configuration list by type (without pagination)
     *
     * @param type
     * @return
     */
    public List<ConfigDTO> listConfigByType(Integer type) {
        List<ConfigDO> configDOs = lambdaQuery()
                .eq(ConfigDO::getType, type)
                .eq(ConfigDO::getStatus, PushStatusEnum.ONLINE.getCode())
                .eq(ConfigDO::getDeleted, YesOrNoEnum.NO.getCode())
                .orderByAsc(ConfigDO::getRank)
                .list();
        return ConfigConverter.toDTOs(configDOs);
    }

    private LambdaQueryChainWrapper<ConfigDO> createConfigQuery(SearchConfigParams params) {
        return lambdaQuery()
                .eq(ConfigDO::getDeleted, YesOrNoEnum.NO.getCode())
                .like(StringUtils.isNotBlank(params.getName()), ConfigDO::getName, params.getName())
                .eq(params.getType() != null && params.getType() != -1, ConfigDO::getType, params.getType());
    }

    /**
     * Get all Banner lists (with pagination)
     *
     * @return
     */
    public List<ConfigDTO> listBanner(SearchConfigParams params) {
        List<ConfigDO> configDOs = createConfigQuery(params)
                .orderByDesc(ConfigDO::getUpdateTime)
                .orderByAsc(ConfigDO::getRank)
                .last(PageParam.getLimitSql(
                        PageParam.newPageInstance(params.getPageNum(), params.getPageSize())))
                .list();
        return ConfigStructMapper.INSTANCE.toDTOs(configDOs);
    }

    /**
     * Get the total count of all Banners (with pagination)
     *
     * @return
     */
    public Long countConfig(SearchConfigParams params) {
        return createConfigQuery(params)
                .count();
    }

    /**
     * Get all notice lists (with pagination)
     *
     * @return
     */
    public List<ConfigDTO> listNotice(PageParam pageParam) {
        List<ConfigDO> configDOs = lambdaQuery()
                .eq(ConfigDO::getType, ConfigTypeEnum.NOTICE.getCode())
                .eq(ConfigDO::getDeleted, YesOrNoEnum.NO.getCode())
                .orderByDesc(ConfigDO::getCreateTime)
                .last(PageParam.getLimitSql(pageParam))
                .list();
        return ConfigConverter.toDTOs(configDOs);
    }

    /**
     * Get the total count of all notices (with pagination)
     *
     * @return
     */
    public Integer countNotice() {
        return lambdaQuery()
                .eq(ConfigDO::getType, ConfigTypeEnum.NOTICE.getCode())
                .eq(ConfigDO::getDeleted, YesOrNoEnum.NO.getCode())
                .count()
                .intValue();
    }

    /**
     * Update reading-related counts
     */
    public void updatePdfConfigVisitNum(long configId, String extra) {
        lambdaUpdate().set(ConfigDO::getExtra, extra)
                .eq(ConfigDO::getId, configId)
                .update();
    }

    public List<GlobalConfigDO> listGlobalConfig(SearchGlobalConfigParams params) {
        LambdaQueryWrapper<GlobalConfigDO> query = buildQuery(params);
        query.select(GlobalConfigDO::getId,
                GlobalConfigDO::getKey,
                GlobalConfigDO::getValue,
                GlobalConfigDO::getComment);
        return globalConfigMapper.selectList(query);
    }

    public Long countGlobalConfig(SearchGlobalConfigParams params) {
        return globalConfigMapper.selectCount(buildQuery(params));
    }

    private LambdaQueryWrapper<GlobalConfigDO> buildQuery(SearchGlobalConfigParams params) {
        LambdaQueryWrapper<GlobalConfigDO> query = Wrappers.lambdaQuery();

        query.and(!StringUtils.isEmpty(params.getKey()),
                        k -> k.like(GlobalConfigDO::getKey, params.getKey()))
                .and(!StringUtils.isEmpty(params.getValue()),
                        v -> v.like(GlobalConfigDO::getValue, params.getValue()))
                .and(!StringUtils.isEmpty(params.getComment()),
                        c -> c.like(GlobalConfigDO::getComment, params.getComment()))
                .eq(GlobalConfigDO::getDeleted, YesOrNoEnum.NO.getCode())
                .orderByDesc(GlobalConfigDO::getUpdateTime);
        return query;
    }

    public void save(GlobalConfigDO globalConfigDO) {
        globalConfigMapper.insert(globalConfigDO);
    }

    public void updateById(GlobalConfigDO globalConfigDO) {
        globalConfigDO.setUpdateTime(new Date());
        globalConfigMapper.updateById(globalConfigDO);
    }

    /**
     * Query global configuration by id
     *
     * @param id
     * @return
     */
    public GlobalConfigDO getGlobalConfigById(Long id) {
        // When querying, 'deleted' is 0
        LambdaQueryWrapper<GlobalConfigDO> query = Wrappers.lambdaQuery();
        query.select(GlobalConfigDO::getId, GlobalConfigDO::getKey, GlobalConfigDO::getValue, GlobalConfigDO::getComment)
                .eq(GlobalConfigDO::getId, id)
                .eq(GlobalConfigDO::getDeleted, YesOrNoEnum.NO.getCode());
        return globalConfigMapper.selectOne(query);
    }

    /**
     * Query global configuration by key
     *
     * @param key
     * @return
     */
    public GlobalConfigDO getGlobalConfigByKey(String key) {
        // When querying, 'deleted' is 0
        LambdaQueryWrapper<GlobalConfigDO> query = Wrappers.lambdaQuery();
        query.select(GlobalConfigDO::getId, GlobalConfigDO::getKey, GlobalConfigDO::getValue, GlobalConfigDO::getComment)
                .eq(GlobalConfigDO::getKey, key)
                .eq(GlobalConfigDO::getDeleted, YesOrNoEnum.NO.getCode());
        return globalConfigMapper.selectOne(query);
    }

    public void delete(GlobalConfigDO globalConfigDO) {
        globalConfigDO.setDeleted(YesOrNoEnum.YES.getCode());
        globalConfigMapper.updateById(globalConfigDO);
    }
}
