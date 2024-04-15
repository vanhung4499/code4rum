package com.hnv99.forum.service.article.repository.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnv99.forum.api.model.enums.YesOrNoEnum;
import com.hnv99.forum.api.model.vo.article.dto.TagDTO;
import com.hnv99.forum.service.article.repository.entity.ArticleTagDO;
import com.hnv99.forum.service.article.repository.mapper.ArticleTagMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Mapper interface for article tags
 *
 * @author louzai
 * @date 2022-07-18
 */
@Repository
public class ArticleTagDao extends ServiceImpl<ArticleTagMapper, ArticleTagDO> {


    /**
     * Batch save
     *
     * @param articleId
     * @param tags
     */
    public void batchSave(Long articleId, Collection<Long> tags) {
        List<ArticleTagDO> insertList = new ArrayList<>(tags.size());
        tags.forEach(s -> {
            ArticleTagDO tag = new ArticleTagDO();
            tag.setTagId(s);
            tag.setArticleId(articleId);
            tag.setDeleted(YesOrNoEnum.NO.getCode());
            insertList.add(tag);
        });
        saveBatch(insertList);
    }


    /**
     * Update article tags
     * 1. If the tag existed but not in the new set, then delete the old tag
     * 2. If the tag existed and changed, then replace the old one
     * 3. If the tag didn't exist but in the new set, then insert it
     *
     * @param articleId
     * @param newTags
     */
    public void updateTags(Long articleId, Set<Long> newTags) {
        List<ArticleTagDO> dbTags = listArticleTags(articleId);
        // Tags in the old set but not in the new set are marked for deletion
        List<Long> toDeleted = new ArrayList<>();
        dbTags.forEach(tag -> {
            if (!newTags.contains(tag.getTagId())) {
                toDeleted.add(tag.getId());
            } else {
                // Remove existing records
                newTags.remove(tag.getTagId());
            }
        });
        if (!toDeleted.isEmpty()) {
            baseMapper.deleteBatchIds(toDeleted);
        }

        if (!newTags.isEmpty()) {
            batchSave(articleId, newTags);
        }
    }


    /**
     * Query article tags
     *
     * @param articleId
     * @return
     */
    public List<TagDTO> queryArticleTagDetails(Long articleId) {
        return baseMapper.listArticleTagDetails(articleId);
    }


    public List<ArticleTagDO> listArticleTags(@Param("articleId") Long articleId) {
        return lambdaQuery().eq(ArticleTagDO::getArticleId, articleId).eq(ArticleTagDO::getDeleted, YesOrNoEnum.NO.getCode()).list();
    }
}

