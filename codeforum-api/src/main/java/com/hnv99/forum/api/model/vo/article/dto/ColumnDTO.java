package com.hnv99.forum.api.model.vo.article.dto;

import com.hnv99.forum.api.model.enums.column.ColumnStatusEnum;
import com.hnv99.forum.api.model.enums.column.ColumnTypeEnum;
import com.hnv99.forum.api.model.vo.user.dto.ColumnFootCountDTO;
import lombok.Data;

@Data
public class ColumnDTO {

    /**
     * Column ID
     */
    private Long columnId;

    /**
     * Column name
     */
    private String column;

    /**
     * Description
     */
    private String introduction;

    /**
     * Cover
     */
    private String cover;

    /**
     * Publish time
     */
    private Long publishTime;

    /**
     * Sorting
     */
    private Integer section;

    /**
     * 0 unpublished, 1 serialization, 2 finished
     *
     * @see ColumnStatusEnum#getCode()
     */
    private Integer state;

    /**
     * Estimated number of articles in the column
     */
    private Integer nums;

    /**
     * Column type
     *
     * @see ColumnTypeEnum#getType()
     */
    private Integer type;

    /**
     * Limited free start time
     */
    private Long freeStartTime;

    /**
     * Limited free end time
     */
    private Long freeEndTime;

    /**
     * Author ID
     */
    private Long author;

    /**
     * Author name
     */
    private String authorName;

    /**
     * Author avatar
     */
    private String authorAvatar;

    /**
     * Author profile
     */
    private String authorProfile;

    /**
     * Statistics related information
     */
    private ColumnFootCountDTO count;
}

