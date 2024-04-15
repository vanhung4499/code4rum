package com.hnv99.forum.front.article.vo;

import com.hnv99.forum.api.model.vo.article.dto.ArticleDTO;
import com.hnv99.forum.api.model.vo.article.dto.CategoryDTO;
import com.hnv99.forum.api.model.vo.article.dto.TagDTO;
import lombok.Data;

import java.util.List;

/**
 * Article Edit View Object
 * Represents the data required for editing an article including the article itself,
 * categories, and tags associated with the article.
 */
@Data
public class ArticleEditVo {

    /**
     * The article to be edited
     */
    private ArticleDTO article;

    /**
     * List of categories available for the article
     */
    private List<CategoryDTO> categories;

    /**
     * List of tags associated with the article
     */
    private List<TagDTO> tags;

}

