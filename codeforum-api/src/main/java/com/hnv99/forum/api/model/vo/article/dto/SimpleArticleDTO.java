package com.hnv99.forum.api.model.vo.article.dto;

import com.hnv99.forum.api.model.enums.column.ColumnArticleReadEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Article recommendation
 */
@Data
@Accessors(chain = true)
public class SimpleArticleDTO implements Serializable {
    private static final long serialVersionUID = 3646376715620165839L;

    @ApiModelProperty("Article ID")
    private Long id;

    @ApiModelProperty("Article title")
    private String title;

    @ApiModelProperty("Column ID")
    private Long columnId;

    @ApiModelProperty("Column title")
    private String column;

    @ApiModelProperty("Article sorting")
    private Integer sort;

    @ApiModelProperty("Creation time")
    private Timestamp createTime;

    /**
     * @see ColumnArticleReadEnum#getRead()
     */
    @ApiModelProperty("Read mode")
    private Integer readType;
}

