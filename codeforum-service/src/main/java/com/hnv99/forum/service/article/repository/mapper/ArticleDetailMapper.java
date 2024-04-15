package com.hnv99.forum.service.article.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnv99.forum.service.article.repository.entity.ArticleDetailDO;
import org.apache.ibatis.annotations.Update;

/**
 * Mapper interface for article details
 */
public interface ArticleDetailMapper extends BaseMapper<ArticleDetailDO> {

    /**
     * Update content
     * fixme: Version iteration has not been managed here yet; if there is an intermediate state of review in the future, for articles that have been published, modifying the content will generate a new record under review with version +1, rather than directly incrementing the version in the original record.
     *
     * @param articleId
     * @param content
     * @return
     */
    @Update("update article_detail set `content` = #{content}, `version` = `version` + 1 where article_id = #{articleId} and `deleted`=0 order by `version` desc limit 1")
    int updateContent(long articleId, String content);
}

