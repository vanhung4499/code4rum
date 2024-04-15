package com.hnv99.forum.service.article.repository.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnv99.forum.api.model.enums.column.ColumnStatusEnum;
import com.hnv99.forum.api.model.exception.ExceptionUtil;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.article.dto.ColumnArticleDTO;
import com.hnv99.forum.api.model.vo.article.dto.SimpleArticleDTO;
import com.hnv99.forum.api.model.vo.constants.StatusEnum;
import com.hnv99.forum.service.article.repository.entity.ColumnArticleDO;
import com.hnv99.forum.service.article.repository.entity.ColumnInfoDO;
import com.hnv99.forum.service.article.repository.mapper.ColumnArticleMapper;
import com.hnv99.forum.service.article.repository.mapper.ColumnInfoMapper;
import com.hnv99.forum.service.article.repository.params.SearchColumnArticleParams;
import com.hnv99.forum.service.article.repository.params.SearchColumnParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Column Repository
 */
@Repository
public class ColumnDao extends ServiceImpl<ColumnInfoMapper, ColumnInfoDO> {

    @Autowired
    private ColumnArticleMapper columnArticleMapper;

    /**
     * Paginated query for column list
     *
     * @param pageParam Page parameters
     * @return List of ColumnInfoDO
     */
    public List<ColumnInfoDO> listOnlineColumns(PageParam pageParam) {
        LambdaQueryWrapper<ColumnInfoDO> query = Wrappers.lambdaQuery();
        query.gt(ColumnInfoDO::getState, ColumnStatusEnum.OFFLINE.getCode())
                .last(PageParam.getLimitSql(pageParam))
                .orderByAsc(ColumnInfoDO::getSection);
        return baseMapper.selectList(query);
    }

    /**
     * Count the number of articles in a column
     *
     * @param columnId ID of the column
     * @return Number of articles in the column
     */
    public int countColumnArticles(Long columnId) {
        LambdaQueryWrapper<ColumnArticleDO> query = Wrappers.lambdaQuery();
        query.eq(ColumnArticleDO::getColumnId, columnId);
        return columnArticleMapper.selectCount(query).intValue();
    }

    public Long countColumnArticles() {
        return columnArticleMapper.selectCount(Wrappers.emptyWrapper());
    }

    /**
     * Count the number of readers in a column
     *
     * @param columnId ID of the column
     * @return Number of readers in the column
     */
    public int countColumnReadPeoples(Long columnId) {
        return columnArticleMapper.countColumnReadUserNums(columnId).intValue();
    }

    /**
     * Retrieve detailed information about articles based on column ID
     *
     * @param params    SearchColumnArticleParams object containing search parameters
     * @param pageParam Page parameters
     * @return List of ColumnArticleDTO
     */
    public List<ColumnArticleDTO> listColumnArticlesDetail(SearchColumnArticleParams params,
                                                           PageParam pageParam) {
        return columnArticleMapper.listColumnArticlesByColumnIdArticleName(params.getColumnId(),
                params.getArticleTitle(),
                pageParam);
    }

    public Integer countColumnArticles(SearchColumnArticleParams params) {
        return columnArticleMapper.countColumnArticlesByColumnIdArticleName(params.getColumnId(),
                params.getArticleTitle()).intValue();
    }

    /**
     * Retrieve article IDs based on column ID
     *
     * @param columnId ID of the column
     * @return List of SimpleArticleDTO
     */
    public List<SimpleArticleDTO> listColumnArticles(Long columnId) {
        return columnArticleMapper.listColumnArticles(columnId);
    }

    public ColumnArticleDO getColumnArticleId(long columnId, Integer section) {
        return columnArticleMapper.getColumnArticle(columnId, section);
    }

    /**
     * Delete column
     *
     * FIXME: Change to logical deletion
     *
     * @param columnId ID of the column
     */
    public void deleteColumn(Long columnId) {
        ColumnInfoDO columnInfoDO = baseMapper.selectById(columnId);
        if (columnInfoDO != null) {
            // If the column has associated articles, do not allow deletion
            // Count the number of articles in the column
            int count = countColumnArticles(columnId);
            if (count > 0) {
                throw ExceptionUtil.of(StatusEnum.COLUMN_ARTICLE_EXISTS, "Please delete tutorials first");
            }

            // Delete the column
            baseMapper.deleteById(columnId);
        }
    }

    /**
     * Query columns
     */
    public List<ColumnInfoDO> listColumnsByParams(SearchColumnParams params, PageParam pageParam) {
        LambdaQueryWrapper<ColumnInfoDO> query = Wrappers.lambdaQuery();
        // Add null check condition
        query.like(StringUtils.isNotBlank(params.getColumn()), ColumnInfoDO::getColumnName, params.getColumn());
        query.last(PageParam.getLimitSql(pageParam))
                .orderByAsc(ColumnInfoDO::getSection)
                .orderByDesc(ColumnInfoDO::getUpdateTime);
        return baseMapper.selectList(query);

    }

    /**
     * Count columns
     */
    public Integer countColumnsByParams(SearchColumnParams params) {
        LambdaQueryWrapper<ColumnInfoDO> query = Wrappers.lambdaQuery();
        lambdaQuery().like(StringUtils.isNotBlank(params.getColumn()), ColumnInfoDO::getColumnName, params.getColumn());
        return baseMapper.selectCount(query).intValue();
    }
}
