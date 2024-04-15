package com.hnv99.forum.front.article.vo;

import com.hnv99.forum.api.model.vo.PageListVo;
import com.hnv99.forum.api.model.vo.article.dto.ArticleDTO;
import lombok.Data;

/**
 * Article List View Object
 * Represents a list of articles categorized by archive type and ID,
 * along with the paginated list of articles.
 */
@Data
public class ArticleListVo {

    /**
     * Archive type
     */
    private String archives;

    /**
     * Archive ID
     */
    private Long archiveId;

    /**
     * Paginated list of articles
     */
    private PageListVo<ArticleDTO> articles;
}
