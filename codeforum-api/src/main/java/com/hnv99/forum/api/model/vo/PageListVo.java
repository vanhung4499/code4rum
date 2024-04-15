package com.hnv99.forum.api.model.vo;

import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Page List View Object
 */
@Data
public class PageListVo<T> {

    /**
     * User list
     */
    List<T> list;

    /**
     * Whether there are more items
     */
    private Boolean hasMore;

    /**
     * Creates an empty PageListVo instance
     */
    public static <T> PageListVo<T> emptyVo() {
        PageListVo<T> vo = new PageListVo<>();
        vo.setList(Collections.emptyList());
        vo.setHasMore(false);
        return vo;
    }

    /**
     * Creates a new PageListVo instance with the given list and page size
     */
    public static <T> PageListVo<T> newVo(List<T> list, long pageSize) {
        PageListVo<T> vo = new PageListVo<>();
        vo.setList(Optional.ofNullable(list).orElse(Collections.emptyList()));
        vo.setHasMore(vo.getList().size() == pageSize);
        return vo;
    }
}
