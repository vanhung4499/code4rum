package com.hnv99.forum.service.article.repository.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnv99.forum.api.model.enums.PushStatusEnum;
import com.hnv99.forum.api.model.enums.YesOrNoEnum;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.article.dto.TagDTO;
import com.hnv99.forum.service.article.converter.ArticleConverter;
import com.hnv99.forum.service.article.repository.entity.TagDO;
import com.hnv99.forum.service.article.repository.mapper.TagMapper;
import com.hnv99.forum.service.article.repository.params.SearchTagParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Tag Repository
 */
@Repository
public class TagDao extends ServiceImpl<TagMapper, TagDO> {

    /**
     * Retrieve a paginated list of online tags
     *
     * @param key       Search key
     * @param pageParam Page parameters
     * @return List of TagDTO
     */
    public List<TagDTO> listOnlineTag(String key, PageParam pageParam) {
        LambdaQueryWrapper<TagDO> query = Wrappers.lambdaQuery();
        query.eq(TagDO::getStatus, PushStatusEnum.ONLINE.getCode())
                .eq(TagDO::getDeleted, YesOrNoEnum.NO.getCode())
                .and(StringUtils.isNotBlank(key), v -> v.like(TagDO::getTagName, key))
                .orderByDesc(TagDO::getId);
        if (pageParam != null) {
            query.last(PageParam.getLimitSql(pageParam));
        }
        List<TagDO> list = baseMapper.selectList(query);
        return ArticleConverter.toDtoList(list);
    }

    /**
     * Count the total number of online tags
     *
     * @param key Search key
     * @return Total count of online tags
     */
    public Integer countOnlineTag(String key) {
        return lambdaQuery()
                .eq(TagDO::getStatus, PushStatusEnum.ONLINE.getCode())
                .eq(TagDO::getDeleted, YesOrNoEnum.NO.getCode())
                .and(!StringUtils.isEmpty(key), v -> v.like(TagDO::getTagName, key))
                .count()
                .intValue();
    }

    private LambdaQueryChainWrapper<TagDO> createTagQuery(SearchTagParams params) {
        return lambdaQuery()
                .eq(TagDO::getDeleted, YesOrNoEnum.NO.getCode())
                .apply(StringUtils.isNotBlank(params.getTag()),
                        "LOWER(tag_name) LIKE {0}",
                        "%" + params.getTag().toLowerCase() + "%");
    }

    /**
     * Retrieve a paginated list of all tags
     *
     * @param params SearchTagParams object containing search parameters
     * @return List of TagDO
     */
    public List<TagDO> listTag(SearchTagParams params) {
        List<TagDO> list = createTagQuery(params)
                .orderByDesc(TagDO::getUpdateTime)
                .last(PageParam.getLimitSql(
                        PageParam.newPageInstance(params.getPageNum(), params.getPageSize())
                ))
                .list();
        return list;
    }

    /**
     * Count the total number of tags
     *
     * @param params SearchTagParams object containing search parameters
     * @return Total count of tags
     */
    public Long countTag(SearchTagParams params) {
        return createTagQuery(params)
                .count();
    }

    /**
     * Retrieve the tag ID by tag name
     *
     * @param tag Tag name
     * @return Tag ID
     */
    public Long selectTagIdByTag(String tag) {
        TagDO record = lambdaQuery().select(TagDO::getId)
                .eq(TagDO::getDeleted, YesOrNoEnum.NO.getCode())
                .eq(TagDO::getTagName, tag)
                .last("limit 1")
                .one();
        return record != null ? record.getId() : null;
    }

    /**
     * Retrieve the tag by ID
     *
     * @param tagId Tag ID
     * @return TagDTO
     */
    public TagDTO selectById(Long tagId) {
        TagDO tagDO = lambdaQuery().eq(TagDO::getId, tagId).one();
        return ArticleConverter.toDto(tagDO);
    }
}
