package com.hnv99.forum.service.comment.repository.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnv99.forum.api.model.enums.YesOrNoEnum;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.service.comment.repository.entity.CommentDO;
import com.hnv99.forum.service.comment.repository.mapper.CommentMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Comment Data Access Object
 */
@Repository
public class CommentDao extends ServiceImpl<CommentMapper, CommentDO> {

    /**
     * Get the list of top comments for an article
     *
     * @param articleId The ID of the article
     * @param pageParam Paging parameters
     * @return List of top comments
     */
    public List<CommentDO> listTopCommentList(Long articleId, PageParam pageParam) {
        return lambdaQuery()
                .eq(CommentDO::getTopCommentId, 0)
                .eq(CommentDO::getArticleId, articleId)
                .eq(CommentDO::getDeleted, YesOrNoEnum.NO.getCode())
                .last(PageParam.getLimitSql(pageParam))
                .orderByDesc(CommentDO::getId).list();
    }

    /**
     * Get all sub-comments for a list of top comment IDs
     *
     * @param articleId     The ID of the article
     * @param topCommentIds List of top comment IDs
     * @return List of sub-comments
     */
    public List<CommentDO> listSubCommentIdMappers(Long articleId, Collection<Long> topCommentIds) {
        return lambdaQuery()
                .in(CommentDO::getTopCommentId, topCommentIds)
                .eq(CommentDO::getArticleId, articleId)
                .eq(CommentDO::getDeleted, YesOrNoEnum.NO.getCode()).list();
    }

    /**
     * Get the count of valid comments for an article
     *
     * @param articleId The ID of the article
     * @return Number of valid comments
     */
    public int commentCount(Long articleId) {
        QueryWrapper<CommentDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(CommentDO::getArticleId, articleId)
                .eq(CommentDO::getDeleted, YesOrNoEnum.NO.getCode());
        return baseMapper.selectCount(queryWrapper).intValue();
    }

    /**
     * Get the hot comment for an article
     *
     * @param articleId The ID of the article
     * @return The hot comment
     */
    public CommentDO getHotComment(Long articleId) {
        Map<String, Object> map = baseMapper.getHotTopCommentId(articleId);
        if (CollectionUtils.isEmpty(map)) {
            return null;
        }

        return baseMapper.selectById(Long.parseLong(String.valueOf(map.get("top_comment_id"))));
    }
}

