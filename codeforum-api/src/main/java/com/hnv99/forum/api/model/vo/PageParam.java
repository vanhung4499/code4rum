package com.hnv99.forum.api.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Database pagination parameters
 */
@Data
public class PageParam {

    public static final Long DEFAULT_PAGE_NUM = 1L;
    public static final Long DEFAULT_PAGE_SIZE = 10L;

    public static final Long TOP_PAGE_SIZE = 4L;

    @ApiModelProperty("Request page number, counting from 1")
    private long pageNum;

    @ApiModelProperty("Request page size, default is 10")
    private long pageSize;
    private long offset;
    private long limit;

    /**
     * Creates a new PageParam instance with default page number and page size
     */
    public static PageParam newPageInstance() {
        return newPageInstance(DEFAULT_PAGE_NUM, DEFAULT_PAGE_SIZE);
    }

    /**
     * Creates a new PageParam instance with the specified page number and page size
     */
    public static PageParam newPageInstance(Integer pageNum, Integer pageSize) {
        return newPageInstance(pageNum.longValue(), pageSize.longValue());
    }

    /**
     * Creates a new PageParam instance with the specified page number and page size
     */
    public static PageParam newPageInstance(Long pageNum, Long pageSize) {
        if (pageNum == null || pageSize == null) {
            return null;
        }

        final PageParam pageParam = new PageParam();
        pageParam.pageNum = pageNum;
        pageParam.pageSize = pageSize;

        pageParam.offset = (pageNum - 1) * pageSize;
        pageParam.limit = pageSize;

        return pageParam;
    }

    /**
     * Gets the limit SQL clause based on the provided PageParam
     */
    public static String getLimitSql(PageParam pageParam) {
        return String.format("limit %s,%s", pageParam.offset, pageParam.limit);
    }
}
