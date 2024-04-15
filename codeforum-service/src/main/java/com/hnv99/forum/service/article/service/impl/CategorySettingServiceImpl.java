package com.hnv99.forum.service.article.service.impl;

import com.hnv99.forum.api.model.vo.PageVo;
import com.hnv99.forum.api.model.vo.article.CategoryReq;
import com.hnv99.forum.api.model.vo.article.SearchCategoryReq;
import com.hnv99.forum.api.model.vo.article.dto.CategoryDTO;
import com.hnv99.forum.core.util.NumUtil;
import com.hnv99.forum.service.article.converter.CategoryStructMapper;
import com.hnv99.forum.service.article.repository.dao.CategoryDao;
import com.hnv99.forum.service.article.repository.entity.CategoryDO;
import com.hnv99.forum.service.article.repository.params.SearchCategoryParams;
import com.hnv99.forum.service.article.service.CategoryService;
import com.hnv99.forum.service.article.service.CategorySettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the CategorySettingService interface.
 */
@Service
public class CategorySettingServiceImpl implements CategorySettingService {

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private CategoryService categoryService;

    @Override
    public void saveCategory(CategoryReq categoryReq) {
        CategoryDO categoryDO = CategoryStructMapper.INSTANCE.toDO(categoryReq);
        if (NumUtil.nullOrZero(categoryReq.getCategoryId())) {
            categoryDao.save(categoryDO);
        } else {
            categoryDO.setId(categoryReq.getCategoryId());
            categoryDao.updateById(categoryDO);
        }
        categoryService.refreshCache();
    }

    @Override
    public void deleteCategory(Integer categoryId) {
        CategoryDO categoryDO = categoryDao.getById(categoryId);
        if (categoryDO != null){
            categoryDao.removeById(categoryDO);
        }
        categoryService.refreshCache();
    }

    @Override
    public void operateCategory(Integer categoryId, Integer pushStatus) {
        CategoryDO categoryDO = categoryDao.getById(categoryId);
        if (categoryDO != null){
            categoryDO.setStatus(pushStatus);
            categoryDao.updateById(categoryDO);
        }
        categoryService.refreshCache();
    }

    @Override
    public PageVo<CategoryDTO> getCategoryList(SearchCategoryReq req) {
        // Convert
        SearchCategoryParams params = CategoryStructMapper.INSTANCE.toSearchParams(req);
        // Query
        List<CategoryDTO> categoryDTOS = categoryDao.listCategory(params);
        Long totalCount = categoryDao.countCategory(params);
        return PageVo.build(categoryDTOS, params.getPageSize(), params.getPageNum(), totalCount);
    }
}
