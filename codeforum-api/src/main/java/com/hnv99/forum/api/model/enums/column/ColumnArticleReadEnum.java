package com.hnv99.forum.api.model.enums.column;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Reading types of column articles
 */
@Getter
@AllArgsConstructor
public enum ColumnArticleReadEnum {
    COLUMN_TYPE(0, "Using the type of the column"),
    LOGIN(1, "Login required"),
    TIME_FREE(2, "Free"),
    STAR_READ(3, "Star reading"),
    ;

    private int read;

    private String desc;

    private static Map<Integer, ColumnArticleReadEnum> cache;

    static {
        cache = new HashMap<>();
        for (ColumnArticleReadEnum r : values()) {
            cache.put(r.read, r);
        }
    }

    public static ColumnArticleReadEnum valueOf(int val) {
        return cache.getOrDefault(val, COLUMN_TYPE);
    }
}
