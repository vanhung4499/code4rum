package com.hnv99.forum.service.article.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnv99.forum.api.model.vo.article.dto.TagDTO;
import com.hnv99.forum.service.article.repository.entity.ArticleTagDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper interface for article tags
 */
public interface ArticleTagMapper extends BaseMapper<ArticleTagDO> {

    /**
     * Query article tags
     *
     * @param articleId
     * @return
     */
    List<TagDTO> listArticleTagDetails(@Param("articleId") Long articleId);

}

