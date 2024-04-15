package com.hnv99.forum.service.article.service.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hnv99.forum.api.model.enums.YesOrNoEnum;
import com.hnv99.forum.api.model.vo.article.dto.CategoryDTO;
import com.hnv99.forum.service.article.converter.ArticleConverter;
import com.hnv99.forum.service.article.repository.dao.CategoryDao;
import com.hnv99.forum.service.article.repository.entity.CategoryDO;
import com.hnv99.forum.service.article.service.CategoryService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Category Service Implementation
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    /**
     * The categoryCaches holds category information in memory.
     */
    private LoadingCache<Long, CategoryDTO> categoryCaches;

    private final CategoryDao categoryDao;

    /**
     * Constructor injecting CategoryDao dependency.
     *
     * @param categoryDao Instance of CategoryDao
     */
    public CategoryServiceImpl(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    /**
     * Initialization method to set up the categoryCaches cache.
     */
    @PostConstruct
    public void init() {
        categoryCaches = CacheBuilder.newBuilder().maximumSize(300).build(new CacheLoader<Long, CategoryDTO>() {
            @Override
            public CategoryDTO load(@NotNull Long categoryId) throws Exception {
                CategoryDO category = categoryDao.getById(categoryId);
                if (category == null || category.getDeleted() == YesOrNoEnum.YES.getCode()) {
                    return CategoryDTO.EMPTY;
                }
                return new CategoryDTO(categoryId, category.getCategoryName(), category.getRank());
            }
        });
    }

    /**
     * Query category name by category ID.
     *
     * @param categoryId Category ID
     * @return Category name
     */
    @Override
    public String queryCategoryName(Long categoryId) {
        return categoryCaches.getUnchecked(categoryId).getCategory();
    }

    /**
     * Load all categories.
     *
     * @return List of CategoryDTO objects representing all categories
     */
    @Override
    public List<CategoryDTO> loadAllCategories() {
        if (categoryCaches.size() <= 5) {
            refreshCache();
        }
        List<CategoryDTO> list = new ArrayList<>(categoryCaches.asMap().values());
        list.removeIf(s -> s.getCategoryId() <= 0);
        list.sort(Comparator.comparingInt(CategoryDTO::getRank));
        return list;
    }

    /**
     * Refresh the cache with the latest category information from the database.
     */
    @Override
    public void refreshCache() {
        List<CategoryDO> list = categoryDao.listAllCategoriesFromDb();
        categoryCaches.invalidateAll();
        categoryCaches.cleanUp();
        list.forEach(s -> categoryCaches.put(s.getId(), ArticleConverter.toDto(s)));
    }

    /**
     * Query category ID by category name.
     *
     * @param category Category name
     * @return Category ID
     */
    @Override
    public Long queryCategoryId(String category) {
        return categoryCaches.asMap().values().stream()
                .filter(s -> s.getCategory().equalsIgnoreCase(category))
                .findFirst().map(CategoryDTO::getCategoryId).orElse(null);
    }
}
