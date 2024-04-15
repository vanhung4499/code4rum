package com.hnv99.forum.service.config.converter;

import com.hnv99.forum.api.model.vo.banner.ConfigReq;
import com.hnv99.forum.api.model.vo.banner.SearchConfigReq;
import com.hnv99.forum.api.model.vo.banner.dto.ConfigDTO;
import com.hnv99.forum.api.model.vo.config.GlobalConfigReq;
import com.hnv99.forum.api.model.vo.config.SearchGlobalConfigReq;
import com.hnv99.forum.api.model.vo.config.dto.GlobalConfigDTO;
import com.hnv99.forum.service.config.repository.entity.ConfigDO;
import com.hnv99.forum.service.config.repository.entity.GlobalConfigDO;
import com.hnv99.forum.service.config.repository.params.SearchConfigParams;
import com.hnv99.forum.service.config.repository.params.SearchGlobalConfigParams;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Mapper interface for mapping between different types of configuration objects.
 */
@Mapper
public interface ConfigStructMapper {
    // Instance
    ConfigStructMapper INSTANCE = Mappers.getMapper( ConfigStructMapper.class );

    // Mapping from request to parameters
    @Mapping(source = "pageNumber", target = "pageNum")
    SearchConfigParams toSearchParams(SearchConfigReq req);

    // Mapping from request to parameters
    @Mapping(source = "pageNumber", target = "pageNum")
    // Mapping from key to keywords
    @Mapping(source = "keywords", target = "key")
    SearchGlobalConfigParams toSearchGlobalParams(SearchGlobalConfigReq req);

    // Mapping from data object to DTO
    ConfigDTO toDTO(ConfigDO configDO);

    List<ConfigDTO> toDTOs(List<ConfigDO> configDOS);

    ConfigDO toDO(ConfigReq configReq);

    // Mapping from data object to DTO
    // Mapping from key to keywords
    @Mapping(source = "key", target = "keywords")
    GlobalConfigDTO toGlobalDTO(GlobalConfigDO configDO);

    List<GlobalConfigDTO> toGlobalDTOS(List<GlobalConfigDO> configDOS);

    @Mapping(source = "keywords", target = "key")
    GlobalConfigDO toGlobalDO(GlobalConfigReq req);
}
