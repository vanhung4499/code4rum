package com.hnv99.forum.service.user.repository.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnv99.forum.api.model.enums.DocumentTypeEnum;
import com.hnv99.forum.api.model.enums.PraiseStatEnum;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.user.dto.ArticleFootCountDTO;
import com.hnv99.forum.api.model.vo.user.dto.SimpleUserInfoDTO;
import com.hnv99.forum.api.model.vo.user.dto.UserFootStatisticDTO;
import com.hnv99.forum.service.user.repository.entity.UserFootDO;
import com.hnv99.forum.service.user.repository.mapper.UserFootMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * UserFootDao
 */
@Repository
public class UserFootDao extends ServiceImpl<UserFootMapper, UserFootDO> {
    public UserFootDO getByDocumentAndUserId(Long documentId, Integer type, Long userId) {
        LambdaQueryWrapper<UserFootDO> query = Wrappers.lambdaQuery();
        query.eq(UserFootDO::getDocumentId, documentId)
                .eq(UserFootDO::getDocumentType, type)
                .eq(UserFootDO::getUserId, userId);
        return baseMapper.selectOne(query);
    }

    public List<SimpleUserInfoDTO> listDocumentPraisedUsers(Long documentId, Integer type, int size) {
        return baseMapper.listSimpleUserInfosByArticleId(documentId, type, size);
    }

    /**
     * Query the list of articles collected by the user
     *
     * @param userId
     * @param pageParam
     * @return
     */
    public List<Long> listCollectedArticlesByUserId(Long userId, PageParam pageParam) {
        return baseMapper.listCollectedArticlesByUserId(userId, pageParam);
    }


    /**
     * Query the list of articles read by the user
     *
     * @param userId
     * @param pageParam
     * @return
     */
    public List<Long> listReadArticleByUserId(Long userId, PageParam pageParam) {
        return baseMapper.listReadArticleByUserId(userId, pageParam);
    }

    /**
     * Query article count information
     *
     * @param articleId
     * @return
     */
    public ArticleFootCountDTO countArticleByArticleId(Long articleId) {
        return baseMapper.countArticleByArticleId(articleId);
    }

    /**
     * Query article statistics by author
     *
     * @param author
     * @return
     */
    public ArticleFootCountDTO countArticleByUserId(Long author) {
        // Count favorites and likes
        ArticleFootCountDTO count = baseMapper.countArticleByUserId(author);
        Optional.ofNullable(count).ifPresent(s -> s.setReadCount(baseMapper.countArticleReadsByUserId(author)));
        return count;
    }

    /**
     * Query the number of likes for comments
     *
     * @param commentId
     * @return
     */
    public Long countCommentPraise(Long commentId) {
        return lambdaQuery()
                .eq(UserFootDO::getDocumentId, commentId)
                .eq(UserFootDO::getDocumentType, DocumentTypeEnum.COMMENT.getCode())
                .eq(UserFootDO::getPraiseStat, PraiseStatEnum.PRAISE.getCode())
                .count();
    }

    public UserFootStatisticDTO getFootCount() {
        return baseMapper.getFootCount();
    }
}
