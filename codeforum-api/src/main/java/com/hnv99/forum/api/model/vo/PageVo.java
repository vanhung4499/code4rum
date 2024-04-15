package com.hnv99.forum.api.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents paginated data.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageVo<T> {

    /**
     * The list of items.
     */
    private List<T> list;

    /**
     * The size of each page.
     */
    private long pageSize;

    /**
     * The current page number.
     */
    private long pageNum;

    /**
     * The total number of pages.
     */
    private long pageTotal;

    /**
     * The total number of items across all pages.
     */
    private long total;

    /**
     * Constructs a PageVo instance.
     *
     * @param list The list of items.
     * @param pageSize The size of each page.
     * @param pageNum The current page number.
     * @param total The total number of items across all pages.
     */
    public PageVo(List<T> list, long pageSize, long pageNum, long total) {
        this.list = list;
        this.total = total;
        this.pageSize = pageSize;
        this.pageNum = pageNum;
        this.pageTotal = (long) Math.ceil((double) total / pageSize);
    }

    /**
     * Creates a PageVo instance.
     *
     * @param list The list of items.
     * @param pageSize The size of each page.
     * @param pageNum The current page number.
     * @param total The total number of items across all pages.
     * @return PageVo<T>
     */
    public static <T> PageVo<T> build(List<T> list, long pageSize, long pageNum, long total) {
        return new PageVo<>(list, pageSize, pageNum, total);
    }
}
