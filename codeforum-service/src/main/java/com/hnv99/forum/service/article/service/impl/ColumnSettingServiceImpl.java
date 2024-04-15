package com.hnv99.forum.service.article.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hnv99.forum.api.model.exception.ExceptionUtil;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.PageVo;
import com.hnv99.forum.api.model.vo.article.*;
import com.hnv99.forum.api.model.vo.article.dto.ColumnArticleDTO;
import com.hnv99.forum.api.model.vo.article.dto.ColumnDTO;
import com.hnv99.forum.api.model.vo.article.dto.SimpleColumnDTO;
import com.hnv99.forum.api.model.vo.constants.StatusEnum;
import com.hnv99.forum.api.model.vo.user.dto.BaseUserInfoDTO;
import com.hnv99.forum.core.util.NumUtil;
import com.hnv99.forum.service.article.converter.ColumnArticleStructMapper;
import com.hnv99.forum.service.article.converter.ColumnStructMapper;
import com.hnv99.forum.service.article.repository.dao.ArticleDao;
import com.hnv99.forum.service.article.repository.dao.ColumnArticleDao;
import com.hnv99.forum.service.article.repository.dao.ColumnDao;
import com.hnv99.forum.service.article.repository.entity.ArticleDO;
import com.hnv99.forum.service.article.repository.entity.ColumnArticleDO;
import com.hnv99.forum.service.article.repository.entity.ColumnInfoDO;
import com.hnv99.forum.service.article.repository.params.SearchColumnArticleParams;
import com.hnv99.forum.service.article.repository.params.SearchColumnParams;
import com.hnv99.forum.service.article.service.ColumnSettingService;
import com.hnv99.forum.service.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Backend interface for managing columns
 */
@Service
public class ColumnSettingServiceImpl implements ColumnSettingService {

    @Autowired
    private UserService userService;

    @Autowired
    private ColumnArticleDao columnArticleDao;

    @Autowired
    private ColumnDao columnDao;

    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private ColumnStructMapper columnStructMapper;

    @Override
    public void saveColumn(ColumnReq req) {
        ColumnInfoDO columnInfoDO = columnStructMapper.toDo(req);
        if (NumUtil.nullOrZero(req.getColumnId())) {
            columnDao.save(columnInfoDO);
        } else {
            columnInfoDO.setId(req.getColumnId());
            columnDao.updateById(columnInfoDO);
        }
    }


    /**
     * Save an article to the corresponding column
     *
     * @param articleId
     * @param columnId
     */
    public void saveColumnArticle(Long articleId, Long columnId) {
        // Convert parameters
        // When inserting, need to check if it already exists
        ColumnArticleDO exist = columnArticleDao.getOne(Wrappers.<ColumnArticleDO>lambdaQuery()
                .eq(ColumnArticleDO::getArticleId, articleId));
        if (exist != null) {
            if (!Objects.equals(columnId, exist.getColumnId())) {
                // Update
                exist.setColumnId(columnId);
                columnArticleDao.updateById(exist);
            }
        } else {
            // Save the article to the column, increment section by 1
            ColumnArticleDO columnArticleDO = new ColumnArticleDO();
            columnArticleDO.setColumnId(columnId);
            columnArticleDO.setArticleId(articleId);
            // Increment section by +1
            Integer maxSection = columnArticleDao.selectMaxSection(columnId);
            columnArticleDO.setSection(maxSection + 1);
            columnArticleDao.save(columnArticleDO);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveColumnArticle(ColumnArticleReq req) {
        // Convert parameters
        ColumnArticleDO columnArticleDO = ColumnArticleStructMapper.INSTANCE.reqToDO(req);
        if (NumUtil.nullOrZero(columnArticleDO.getId())) {
            // When inserting, need to check if it already exists
            ColumnArticleDO exist = columnArticleDao.getOne(Wrappers.<ColumnArticleDO>lambdaQuery()
                    .eq(ColumnArticleDO::getColumnId, columnArticleDO.getColumnId())
                    .eq(ColumnArticleDO::getArticleId, columnArticleDO.getArticleId()));
            if (exist != null) {
                throw ExceptionUtil.of(StatusEnum.COLUMN_ARTICLE_EXISTS, "Please do not repeat");
            }

            // Increment section by +1
            Integer maxSection = columnArticleDao.selectMaxSection(columnArticleDO.getColumnId());
            columnArticleDO.setSection(maxSection + 1);
            columnArticleDao.save(columnArticleDO);
        } else {
            columnArticleDao.updateById(columnArticleDO);
        }

        // At the same time, update the shortTitle of the article
        if (req.getShortTitle() != null) {
            ArticleDO articleDO = new ArticleDO();
            articleDO.setShortTitle(req.getShortTitle());
            articleDO.setId(req.getArticleId());
            articleDao.updateById(articleDO);
        }
    }

    @Override
    public void deleteColumn(Long columnId) {
        columnDao.deleteColumn(columnId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteColumnArticle(Long id) {
        ColumnArticleDO columnArticleDO = columnArticleDao.getById(id);
        if (columnArticleDO != null) {
            columnArticleDao.removeById(id);
            // When deleting, batch update section, for example, if the original sections were 1,2,3,4,5,6,7,8,9,10 and 5 is deleted, then sections 6-10 should be decremented by 1
            columnArticleDao.update(null, Wrappers.<ColumnArticleDO>lambdaUpdate()
                    .setSql("section = section - 1")
                    .eq(ColumnArticleDO::getColumnId, columnArticleDO.getColumnId())
                    // section greater than 1
                    .gt(ColumnArticleDO::getSection, 1)
                    .gt(ColumnArticleDO::getSection, columnArticleDO.getSection()));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sortColumnArticleApi(SortColumnArticleReq req) {
        // Swap the order of two articles based on the IDs provided in req
        ColumnArticleDO activeDO = columnArticleDao.getById(req.getActiveId());
        ColumnArticleDO overDO = columnArticleDao.getById(req.getOverId());
        if (activeDO != null && overDO != null && !activeDO.getId().equals(overDO.getId())) {
            Integer activeSection = activeDO.getSection();
            Integer overSection = overDO.getSection();
            // If the original order is 1, 2, 3, 4
            //
            // Moving 1 after 4: 2 becomes 1, 3 becomes 2, 4 becomes 3, 1 becomes 4
            // Moving 1 after 3: 2 becomes 1, 3 becomes 2, 4 remains the same, 1 becomes 3
            // Moving 1 after 2: 2 becomes 1, 3 remains the same, 4 remains the same, 1 becomes 2
            // Moving 2 after 4: 1 remains the same, 3 becomes 2, 4 becomes 3, 2 becomes 4
            // Moving 2 after 3: 1 remains the same, 3 becomes 2, 4 remains the same, 2 becomes 3
            // Moving 3 after 4: 1 remains the same, 2 remains the same, 4 becomes 3, 3 becomes 4
            // Moving 4 before 1: 1 becomes 2, 2 becomes 3, 3 becomes 4
            // Moving 4 before 2: 1 remains the same, 2 becomes 3, 3 becomes 4, 4 becomes 1
            // Moving 4 before 3: 1 remains the same, 2 remains the same, 3 becomes 4, 4 becomes 1
            // Moving 3 before 1: 1 becomes 2, 2 becomes 3, 3 becomes 4, 4 becomes 1
            // and so on
            // 1. If activeSection > overSection, then sections from activeSection - 1 to overSection need to be incremented by 1
            // Move upwards
            if (activeSection > overSection) {
                // When activeSection is greater than overSection, it indicates that the article is dragged upwards.
                // Need to increment the section of all articles between activeSection and overSection (excluding activeSection itself) by 1,
                // and set activeSection to overSection.
                columnArticleDao.update(null, Wrappers.<ColumnArticleDO>lambdaUpdate()
                        .setSql("section = section + 1") // Increment the section field of the records that meet the conditions by 1
                        .eq(ColumnArticleDO::getColumnId, overDO.getColumnId()) // Specify the columnId condition for the records to be updated
                        .ge(ColumnArticleDO::getSection, overSection) // Specify the lower limit of the section field (includes this value)
                        .lt(ColumnArticleDO::getSection, activeSection)); // Specify the upper limit of the section field

                // Set the section of activeDO to overSection
                activeDO.setSection(overSection);
                columnArticleDao.updateById(activeDO);
            } else {
                // 2. If activeSection < overSection,
                // then sections from activeSection + 1 to overSection need to be decremented by 1
                // Move downwards
                // Need to decrement the section of all articles between activeSection and overSection (including overSection) by 1
                columnArticleDao.update(null, Wrappers.<ColumnArticleDO>lambdaUpdate()
                        .setSql("section = section - 1") // Decrement the section field of the records that meet the conditions by 1
                        .eq(ColumnArticleDO::getColumnId, overDO.getColumnId()) // Specify the columnId condition for the records to be updated
                        .gt(ColumnArticleDO::getSection, activeSection) // Specify the lower limit of the section field (excludes this value)
                        .le(ColumnArticleDO::getSection, overSection)); // Specify the upper limit of the section field (includes this value)

                // Set the section of activeDO to overSection -1
                activeDO.setSection(overSection);
                columnArticleDao.updateById(activeDO);

            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sortColumnArticleByIDApi(SortColumnArticleByIDReq req) {
        // Get the column article to be reordered
        ColumnArticleDO columnArticleDO = columnArticleDao.getById(req.getId());
        // If not null
        if (columnArticleDO == null) {
            throw ExceptionUtil.of(StatusEnum.COLUMN_ARTICLE_EXISTS, "Tutorial does not exist");
        }
        // If the order has not changed
        if (req.getSort().equals(columnArticleDO.getSection())) {
            return;
        }
        // Get the maximum order that the tutorial can adjust
        Integer maxSection = columnArticleDao.selectMaxSection(columnArticleDO.getColumnId());
        // If the input order is greater than the maximum order, prompt error
        if (req.getSort() > maxSection) {
            throw ExceptionUtil.of(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "Order out of range");
        }
        // Check if the input order exists
        ColumnArticleDO changeColumnArticleDO = columnArticleDao.selectBySection(columnArticleDO.getColumnId(), req.getSort());
        // If it exists, exchange the order
        if (changeColumnArticleDO != null) {
            // Exchange the order
            columnArticleDao.update(null, Wrappers.<ColumnArticleDO>lambdaUpdate()
                    .set(ColumnArticleDO::getSection, columnArticleDO.getSection())
                    .eq(ColumnArticleDO::getId, changeColumnArticleDO.getId()));
            columnArticleDao.update(null, Wrappers.<ColumnArticleDO>lambdaUpdate()
                    .set(ColumnArticleDO::getSection, changeColumnArticleDO.getSection())
                    .eq(ColumnArticleDO::getId, columnArticleDO.getId()));
        } else {
            // If it does not exist, directly modify the order
            throw ExceptionUtil.of(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "The input order does not exist, cannot complete the exchange");
        }
    }

    @Override
    public PageVo<ColumnDTO> getColumnList(SearchColumnReq req) {
        // Convert parameters
        ColumnStructMapper mapper = ColumnStructMapper.INSTANCE;
        SearchColumnParams params = mapper.reqToSearchParams(req);
        // Query
        List<ColumnInfoDO> columnList = columnDao.listColumnsByParams(params, PageParam.newPageInstance(req.getPageNumber(), req.getPageSize()));
        // Convert attributes
        List<ColumnDTO> columnDTOS = mapper.infoToDtos(columnList);

        // Optimization: Instead of querying user information multiple times, change it to one query
        // Get all required user IDs
        // Check if columnDTOS is not empty
        if (CollUtil.isNotEmpty(columnDTOS)) {
            List<Long> userIds = columnDTOS.stream().map(ColumnDTO::getAuthor).collect(Collectors.toList());

            // Query all user information
            List<BaseUserInfoDTO> users = userService.batchQueryBasicUserInfo(userIds);

            // Create a mapping from ID to user information
            Map<Long, BaseUserInfoDTO> userMap = users.stream().collect(Collectors.toMap(BaseUserInfoDTO::getId, Function.identity()));

            // Set author information
            columnDTOS.forEach(columnDTO -> {
                BaseUserInfoDTO user = userMap.get(columnDTO.getAuthor());
                columnDTO.setAuthorName(user.getUserName());
                columnDTO.setAuthorAvatar(user.getPhoto());
                columnDTO.setAuthorProfile(user.getProfile());
            });
        }

        Integer totalCount = columnDao.countColumnsByParams(params);
        return PageVo.build(columnDTOS, req.getPageSize(), req.getPageNumber(), totalCount);
    }

    @Override
    public PageVo<ColumnArticleDTO> getColumnArticleList(SearchColumnArticleReq req) {
        // Convert parameters
        ColumnArticleStructMapper mapper = ColumnArticleStructMapper.INSTANCE;
        SearchColumnArticleParams params = mapper.toSearchParams(req);
        // Query
        List<ColumnArticleDTO> simpleArticleDTOS = columnDao.listColumnArticlesDetail(params, PageParam.newPageInstance(req.getPageNumber(), req.getPageSize()));
        int totalCount = columnDao.countColumnArticles(params);
        return PageVo.build(simpleArticleDTOS, req.getPageSize(), req.getPageNumber(), totalCount);
    }

    @Override
    public List<SimpleColumnDTO> listSimpleColumnBySearchKey(String key) {
        LambdaQueryWrapper<ColumnInfoDO> query = Wrappers.lambdaQuery();
        query.select(ColumnInfoDO::getId, ColumnInfoDO::getColumnName, ColumnInfoDO::getCover)
                .and(!StringUtils.isEmpty(key),
                        v -> v.like(ColumnInfoDO::getColumnName, key)
                )
                .orderByDesc(ColumnInfoDO::getId);
        List<ColumnInfoDO> articleDOS = columnDao.list(query);
        return ColumnStructMapper.INSTANCE.infoToSimpleDtos(articleDOS);
    }

}

