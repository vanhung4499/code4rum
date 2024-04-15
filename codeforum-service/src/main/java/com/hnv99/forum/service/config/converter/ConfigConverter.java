package com.hnv99.forum.service.config.converter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.hnv99.forum.api.model.vo.banner.ConfigReq;
import com.hnv99.forum.api.model.vo.banner.dto.ConfigDTO;
import com.hnv99.forum.service.config.repository.entity.ConfigDO;
import org.springframework.util.CollectionUtils;

/**
 * Utility class for converting ConfigDO objects to ConfigDTO objects and vice versa.
 */
public class ConfigConverter {

    /**
     * Converts a list of ConfigDO objects to a list of ConfigDTO objects.
     *
     * @param records The list of ConfigDO objects
     * @return The list of ConfigDTO objects
     */
    public static List<ConfigDTO> toDTOs(List<ConfigDO> records) {
        if (CollectionUtils.isEmpty(records)) {
            return Collections.emptyList();
        }
        return records.stream().map(ConfigConverter::toDTO).collect(Collectors.toList());
    }

    /**
     * Converts a ConfigDO object to a ConfigDTO object.
     *
     * @param configDO The ConfigDO object
     * @return The ConfigDTO object
     */
    public static ConfigDTO toDTO(ConfigDO configDO) {
        if (configDO == null) {
            return null;
        }
        ConfigDTO configDTO = new ConfigDTO();
        configDTO.setType(configDO.getType());
        configDTO.setName(configDO.getName());
        configDTO.setBannerUrl(configDO.getBannerUrl());
        configDTO.setJumpUrl(configDO.getJumpUrl());
        configDTO.setContent(configDO.getContent());
        configDTO.setRank(configDO.getRank());
        configDTO.setStatus(configDO.getStatus());
        configDTO.setId(configDO.getId());
        configDTO.setTags(configDO.getTags());
        configDTO.setExtra(configDO.getExtra());
        configDTO.setCreateTime(configDO.getCreateTime());
        configDTO.setUpdateTime(configDO.getUpdateTime());
        return configDTO;
    }

    /**
     * Converts a ConfigReq object to a ConfigDO object.
     *
     * @param configReq The ConfigReq object
     * @return The ConfigDO object
     */
    public static ConfigDO toDO(ConfigReq configReq) {
        if (configReq == null) {
            return null;
        }
        ConfigDO configDO = new ConfigDO();
        configDO.setType(configReq.getType());
        configDO.setName(configReq.getName());
        configDO.setBannerUrl(configReq.getBannerUrl());
        configDO.setJumpUrl(configReq.getJumpUrl());
        configDO.setContent(configReq.getContent());
        configDO.setRank(configReq.getRank());
        configDO.setTags(configReq.getTags());
        return configDO;
    }
}

