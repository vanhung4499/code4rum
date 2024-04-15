package com.hnv99.forum.service.article.repository.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnv99.forum.service.article.repository.entity.ColumnArticleDO;
import com.hnv99.forum.service.article.repository.mapper.ColumnArticleMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;


@Repository
public class ColumnArticleDao extends ServiceImpl<ColumnArticleMapper, ColumnArticleDO> {
    @Resource
    private ColumnArticleMapper columnArticleMapper;

    /**
     * Retrieve the maximum number of sections for a column
     *
     * @param columnId The ID of the column
     * @return If there are no articles in the column, return 0; otherwise, return the current maximum section number
     */
    public int selectMaxSection(Long columnId) {
        return columnArticleMapper.selectMaxSection(columnId);
    }

    /**
     * Retrieve the column information based on the article ID
     * FIXME: If an article belongs to multiple columns, there may be issues
     *
     * @param articleId The ID of the article
     * @return ColumnArticleDO object representing the column information
     */
    public ColumnArticleDO selectColumnArticleByArticleId(Long articleId) {
        List<ColumnArticleDO> list = lambdaQuery()
                .eq(ColumnArticleDO::getArticleId, articleId)
                .list();
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    /**
     * Retrieve the column article based on the column ID and section number
     *
     * @param columnId The ID of the column
     * @param sort     The section number
     * @return ColumnArticleDO object representing the column article
     */
    public ColumnArticleDO selectBySection(Long columnId, Integer sort) {
        return lambdaQuery()
                .eq(ColumnArticleDO::getColumnId, columnId)
                .eq(ColumnArticleDO::getSection, sort)
                .one();
    }
}
