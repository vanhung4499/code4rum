package com.hnv99.forum.service.config.converter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.hnv99.forum.api.model.vo.article.dto.DictCommonDTO;
import com.hnv99.forum.service.config.repository.entity.DictCommonDO;
import org.springframework.util.CollectionUtils;

/**
 * Converter class for mapping between different types of DictCommon objects.
 */
public class DictCommonConverter {

    /**
     * Convert a list of DictCommonDO objects to a list of DictCommonDTO objects.
     *
     * @param records List of DictCommonDO objects
     * @return List of DictCommonDTO objects
     */
    public static List<DictCommonDTO> toDTOs(List<DictCommonDO> records) {
        if (CollectionUtils.isEmpty(records)) {
            return Collections.emptyList();
        }
        return records.stream().map(DictCommonConverter::toDTO).collect(Collectors.toList());
    }

    /**
     * Convert a DictCommonDO object to a DictCommonDTO object.
     *
     * @param dictCommonDO DictCommonDO object
     * @return DictCommonDTO object
     */
    public static DictCommonDTO toDTO(DictCommonDO dictCommonDO) {
        if (dictCommonDO == null) {
            return null;
        }
        DictCommonDTO dictCommonDTO = new DictCommonDTO();
        dictCommonDTO.setTypeCode(dictCommonDO.getTypeCode());
        dictCommonDTO.setDictCode(dictCommonDO.getDictCode());
        dictCommonDTO.setDictDesc(dictCommonDO.getDictDesc());
        dictCommonDTO.setSortNo(dictCommonDO.getSortNo());
        return dictCommonDTO;
    }
}
