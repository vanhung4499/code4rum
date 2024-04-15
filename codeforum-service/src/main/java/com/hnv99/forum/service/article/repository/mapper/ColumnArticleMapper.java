package com.hnv99.forum.service.article.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.article.dto.ColumnArticleDTO;
import com.hnv99.forum.api.model.vo.article.dto.SimpleArticleDTO;
import com.hnv99.forum.service.article.repository.entity.ColumnArticleDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Mapper interface for column articles
 */
public interface ColumnArticleMapper extends BaseMapper<ColumnArticleDO> {
    /**
     * Query the list of articles
     *
     * @param columnId The ID of the column
     * @return List of simple article DTOs
     */
    List<SimpleArticleDTO> listColumnArticles(@Param("columnId") Long columnId);

    /**
     * Query an article
     *
     * @param columnId The ID of the column
     * @param section The section of the article
     * @return Column article DO
     */
    ColumnArticleDO getColumnArticle(@Param("columnId") Long columnId, @Param("section") Integer section);


    /**
     * Count the number of readers for the column
     *
     * @param columnId The ID of the column
     * @return Number of readers
     */
    Long countColumnReadUserNums(@Param("columnId") Long columnId);

    /**
     * Query articles by column ID and article name
     *
     * @param columnId The ID of the column
     * @param articleTitle The title of the article
     * @param pageParam Page parameters
     * @return List of column article DTOs
     */
    List<ColumnArticleDTO> listColumnArticlesByColumnIdArticleName(@Param("columnId") Long columnId,
                                                                   @Param("articleTitle") String articleTitle,
                                                                   @Param("pageParam") PageParam pageParam);

    /**
     * Count articles by column ID and article name
     *
     * @param columnId The ID of the column
     * @param articleTitle The title of the article
     * @return Number of articles
     */
    Long countColumnArticlesByColumnIdArticleName(@Param("columnId") Long columnId, @Param("articleTitle") String articleTitle);

    /**
     * Query the maximum section within a column by column ID
     *
     * @param columnId The ID of the column
     * @return The maximum section number
     */
    @Select("select ifnull(max(section), 0) from column_article where column_id = #{columnId}")
    int selectMaxSection(@Param("columnId") Long columnId);
}

