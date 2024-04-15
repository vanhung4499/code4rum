package com.hnv99.forum.service.article.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.article.dto.ArticleAdminDTO;
import com.hnv99.forum.api.model.vo.article.dto.SimpleArticleDTO;
import com.hnv99.forum.api.model.vo.article.dto.YearArticleDTO;
import com.hnv99.forum.service.article.repository.entity.ArticleDO;

import com.hnv99.forum.service.article.repository.entity.ReadCountDO;
import com.hnv99.forum.service.article.repository.params.SearchArticleParams;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Article mapper interface
 */
public interface ArticleMapper extends BaseMapper<ArticleDO> {

    /**
     * Traverse articles by ID, used for generating sitemap.xml
     *
     * @param lastId
     * @param size
     * @return
     */
    List<SimpleArticleDTO> listArticlesOrderById(@Param("lastId") Long lastId, @Param("size") int size);

    /**
     * Get popular articles by reading counts
     *
     * @param pageParam
     * @return
     */
    List<SimpleArticleDTO> listArticlesByReadCounts(@Param("pageParam") PageParam pageParam);

    /**
     * Query popular articles by author
     *
     * @param userId
     * @param pageParam
     * @return
     */
    List<SimpleArticleDTO> listArticlesByUserIdOrderByReadCounts(@Param("userId") Long userId, @Param("pageParam") PageParam pageParam);

    /**
     * Query articles by category and tags
     *
     * @param category
     * @param tagIds
     * @param pageParam
     * @return
     */
    List<ReadCountDO> listArticleByCategoryAndTags(@Param("categoryId") Long category,
                                                   @Param("tags") List<Long> tagIds,
                                                   @Param("pageParam") PageParam pageParam);

    /**
     * Get creative history by user ID
     *
     * @param userId
     * @return
     */
    List<YearArticleDTO> listYearArticleByUserId(@Param("userId") Long userId);

    List<ArticleAdminDTO> listArticlesByParams(@Param("searchParams") SearchArticleParams searchArticleParams,
                                               @Param("pageParam") PageParam pageParam);

    Long countArticlesByParams(@Param("searchParams") SearchArticleParams searchArticleParams);
}
