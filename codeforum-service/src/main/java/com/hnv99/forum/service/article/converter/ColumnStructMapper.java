package com.hnv99.forum.service.article.converter;

import com.hnv99.forum.api.model.vo.article.ColumnReq;
import com.hnv99.forum.api.model.vo.article.SearchColumnReq;
import com.hnv99.forum.api.model.vo.article.dto.ColumnDTO;
import com.hnv99.forum.api.model.vo.article.dto.SimpleColumnDTO;
import com.hnv99.forum.service.article.repository.entity.ColumnInfoDO;
import com.hnv99.forum.service.article.repository.params.SearchColumnParams;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ColumnStructMapper {
    ColumnStructMapper INSTANCE = Mappers.getMapper(ColumnStructMapper.class);

    /**
     * Convert SearchColumnReq to SearchColumnParams
     * @param req
     * @return
     */
    SearchColumnParams reqToSearchParams(SearchColumnReq req);

    /**
     * Map ColumnInfoDO to ColumnDTO
     * @param columnInfoDO
     * @return
     */
    // sources are parameters, targets are destinations
    @Mapping(source = "id", target = "columnId")
    @Mapping(source = "columnName", target = "column")
    @Mapping(source = "userId", target = "author")
    // Convert Date to Long
    @Mapping(target = "publishTime", expression = "java(columnInfoDO.getPublishTime().getTime())")
    @Mapping(target = "freeStartTime", expression = "java(columnInfoDO.getFreeStartTime().getTime())")
    @Mapping(target = "freeEndTime", expression = "java(columnInfoDO.getFreeEndTime().getTime())")
    ColumnDTO infotoDto(ColumnInfoDO columnInfoDO);

    List<ColumnDTO> infoToDtos(List<ColumnInfoDO> columnInfoDOs);


    /**
     * Map ColumnInfoDO to SimpleColumnDTO
     * @param columnInfoDO
     * @return
     */
    // Map column ID, column name, and cover
    @Mapping(source = "id", target = "columnId")
    @Mapping(source = "columnName", target = "column")
    SimpleColumnDTO infoToSimpleDto(ColumnInfoDO columnInfoDO);

    List<SimpleColumnDTO> infoToSimpleDtos(List<ColumnInfoDO> columnInfoDOs);

    @Mapping(source = "column", target = "columnName")
    @Mapping(source = "author", target = "userId")
    // Convert Long to Date
    @Mapping(target = "freeStartTime", expression = "java(new java.util.Date(req.getFreeStartTime()))")
    @Mapping(target = "freeEndTime", expression = "java(new java.util.Date(req.getFreeEndTime()))")
    ColumnInfoDO toDo(ColumnReq req);
}

