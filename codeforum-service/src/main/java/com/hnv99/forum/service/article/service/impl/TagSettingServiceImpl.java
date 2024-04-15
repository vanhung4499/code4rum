package com.hnv99.forum.service.article.service.impl;

import com.hnv99.forum.api.model.vo.PageVo;
import com.hnv99.forum.api.model.vo.article.SearchTagReq;
import com.hnv99.forum.api.model.vo.article.TagReq;
import com.hnv99.forum.api.model.vo.article.dto.TagDTO;
import com.hnv99.forum.core.cache.RedisClient;
import com.hnv99.forum.core.util.JsonUtil;
import com.hnv99.forum.core.util.NumUtil;
import com.hnv99.forum.service.article.converter.TagStructMapper;
import com.hnv99.forum.service.article.repository.dao.TagDao;
import com.hnv99.forum.service.article.repository.entity.TagDO;
import com.hnv99.forum.service.article.repository.params.SearchTagParams;
import com.hnv99.forum.service.article.service.TagSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Backend interface for managing tags
 */
@Service
public class TagSettingServiceImpl implements TagSettingService {

    private static final String CACHE_TAG_PRE = "cache_tag_pre_";

    private static final Long CACHE_TAG_EXPIRE_TIME = 100L;

    @Autowired
    private TagDao tagDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveTag(TagReq tagReq) {
        TagDO tagDO = TagStructMapper.INSTANCE.toDO(tagReq);

        // First write to MySQL
        if (NumUtil.nullOrZero(tagReq.getTagId())) {
            tagDao.save(tagDO);
        } else {
            tagDO.setId(tagReq.getTagId());
            tagDao.updateById(tagDO);
        }

        // Then delete from Redis
        String redisKey = CACHE_TAG_PRE + tagDO.getId();
        RedisClient.del(redisKey);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTag(Integer tagId) {
        TagDO tagDO = tagDao.getById(tagId);
        if (tagDO != null){
            // First write to MySQL
            tagDao.removeById(tagId);

            // Then delete from Redis
            String redisKey = CACHE_TAG_PRE + tagDO.getId();
            RedisClient.del(redisKey);
        }
    }

    @Override
    public void operateTag(Integer tagId, Integer pushStatus) {
        TagDO tagDO = tagDao.getById(tagId);
        if (tagDO != null){

            // First write to MySQL
            tagDO.setStatus(pushStatus);
            tagDao.updateById(tagDO);

            // Then delete from Redis
            String redisKey = CACHE_TAG_PRE + tagDO.getId();
            RedisClient.del(redisKey);
        }
    }

    @Override
    public PageVo<TagDTO> getTagList(SearchTagReq req) {
        // Convert
        SearchTagParams params = TagStructMapper.INSTANCE.toSearchParams(req);
        // Query
        List<TagDTO> tagDTOs = TagStructMapper.INSTANCE.toDTOs(tagDao.listTag(params));
        Long totalCount = tagDao.countTag(params);
        return PageVo.build(tagDTOs, params.getPageSize(), params.getPageNum(), totalCount);
    }

    @Override
    public TagDTO getTagById(Long tagId) {

        String redisKey = CACHE_TAG_PRE + tagId;

        // First check cache, if exists, return directly
        String tagInfoStr = RedisClient.getStr(redisKey);
        if (tagInfoStr != null && !tagInfoStr.isEmpty()) {
            return JsonUtil.toObj(tagInfoStr, TagDTO.class);
        }

        // If not found in cache, first query DB, then write to cache
        TagDTO tagDTO = tagDao.selectById(tagId);
        tagInfoStr = JsonUtil.toStr(tagDTO);
        RedisClient.setStrWithExpire(redisKey, tagInfoStr, CACHE_TAG_EXPIRE_TIME);

        return tagDTO;
    }
}
