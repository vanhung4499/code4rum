package com.hnv99.forum.service.article.repository.dao;


import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnv99.forum.api.model.enums.PushStatusEnum;
import com.hnv99.forum.api.model.enums.YesOrNoEnum;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.article.dto.CategoryDTO;
import com.hnv99.forum.service.article.converter.CategoryStructMapper;
import com.hnv99.forum.service.article.repository.entity.CategoryDO;
import com.hnv99.forum.service.article.repository.mapper.CategoryMapper;
import com.hnv99.forum.service.article.repository.params.SearchCategoryParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Category Dao
 *
 * @author louzai
 * @date 2022-07-20
 */
@Repository
public class CategoryDao extends ServiceImpl<CategoryMapper, CategoryDO> {
    /**
     * Retrieve all categories from the database
     *
     * @return List of CategoryDO
     */
    public List<CategoryDO> listAllCategoriesFromDb() {
        return lambdaQuery()
                .eq(CategoryDO::getDeleted, YesOrNoEnum.NO.getCode())
                .eq(CategoryDO::getStatus, PushStatusEnum.ONLINE.getCode())
                .list();
    }

    // A private method to construct query conditions
    private LambdaQueryChainWrapper<CategoryDO> createCategoryQuery(SearchCategoryParams params) {
        return lambdaQuery()
                .eq(CategoryDO::getDeleted, YesOrNoEnum.NO.getCode())
                .like(StringUtils.isNotBlank(params.getCategory()), CategoryDO::getCategoryName, params.getCategory());
    }

    /**
     * Retrieve a paginated list of all categories
     *
     * @param params SearchCategoryParams object containing search parameters
     * @return List of CategoryDTO
     */
    public List<CategoryDTO> listCategory(SearchCategoryParams params) {
        List<CategoryDO> list = createCategoryQuery(params)
                .orderByDesc(CategoryDO::getUpdateTime)
                .orderByAsc(CategoryDO::getRank)
                .last(PageParam.getLimitSql(
                        PageParam.newPageInstance(params.getPageNum(), params.getPageSize())
                ))
                .list();
        return CategoryStructMapper.INSTANCE.toDTOs(list);
    }

    /**
     * Retrieve the total count of all categories (paginated)
     *
     * @param params SearchCategoryParams object containing search parameters
     * @return Total count of categories
     */
    public Long countCategory(SearchCategoryParams params) {
        return createCategoryQuery(params)
                .count();
    }
}

