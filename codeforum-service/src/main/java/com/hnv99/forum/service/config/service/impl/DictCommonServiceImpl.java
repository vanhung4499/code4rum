package com.hnv99.forum.service.config.service.impl;

import com.google.common.collect.Maps;
import com.hnv99.forum.api.model.vo.article.dto.CategoryDTO;
import com.hnv99.forum.api.model.vo.article.dto.DictCommonDTO;
import com.hnv99.forum.service.article.service.CategoryService;
import com.hnv99.forum.service.config.repository.dao.DictCommonDao;
import com.hnv99.forum.service.config.service.DictCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dictionary Service Implementation
 */
@Service
public class DictCommonServiceImpl implements DictCommonService {

    @Resource
    private DictCommonDao dictCommonDao;

    @Autowired
    private CategoryService categoryService;

    @Override
    public Map<String, Object> getDict() {
        Map<String, Object> result = Maps.newLinkedHashMap();

        List<DictCommonDTO> dictCommonList = dictCommonDao.getDictList();

        Map<String, Map<String, String>> dictCommonMap = Maps.newLinkedHashMap();
        for (DictCommonDTO dictCommon : dictCommonList) {
            Map<String, String> codeMap = dictCommonMap.get(dictCommon.getTypeCode());
            if (codeMap == null || codeMap.isEmpty()) {
                codeMap = Maps.newLinkedHashMap();
                dictCommonMap.put(dictCommon.getTypeCode(), codeMap);
            }
            codeMap.put(dictCommon.getDictCode(), dictCommon.getDictDesc());
        }

        // Get dictionary information for categories
        List<CategoryDTO> categoryDTOS = categoryService.loadAllCategories();
        Map<String, String> codeMap = new HashMap<>();
        categoryDTOS.forEach(categoryDTO -> codeMap.put(categoryDTO.getCategoryId().toString(), categoryDTO.getCategory()));
        dictCommonMap.put("CategoryType", codeMap);

        result.putAll(dictCommonMap);
        return result;
    }

}

