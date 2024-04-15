package com.hnv99.forum.service.article.service.impl;

import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.PageVo;
import com.hnv99.forum.api.model.vo.article.dto.TagDTO;
import com.hnv99.forum.service.article.repository.dao.TagDao;
import com.hnv99.forum.service.article.service.TagService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    private final TagDao tagDao;

    public TagServiceImpl(TagDao tagDao) {
        this.tagDao = tagDao;
    }

    @Override
    public PageVo<TagDTO> queryTags(String key, PageParam pageParam) {
        List<TagDTO> tagDTOs = tagDao.listOnlineTag(key, pageParam);
        Integer totalCount = tagDao.countOnlineTag(key);
        return PageVo.build(tagDTOs, pageParam.getPageSize(), pageParam.getPageNum(), totalCount);
    }

    @Override
    public Long queryTagId(String tag) {
        return tagDao.selectTagIdByTag(tag);
    }
}
