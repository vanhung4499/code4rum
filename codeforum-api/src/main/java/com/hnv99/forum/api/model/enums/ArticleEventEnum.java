package com.hnv99.forum.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum for article events
 */
@Getter
@AllArgsConstructor
public enum ArticleEventEnum {
    CREATE(1, "Create"),
    ONLINE(2, "Publish"),
    REVIEW(3, "Review"),
    DELETE(4, "Delete"),
    OFFLINE(5, "Offline"),
    ;


    private int type;
    private String msg;

    private static Map<Integer, ArticleEventEnum> mapper;

    static {
        mapper = new HashMap<>();
        for (ArticleEventEnum type : values()) {
            mapper.put(type.type, type);
        }
    }

    public static ArticleEventEnum typeOf(int type) {
        return mapper.get(type);
    }

    public static ArticleEventEnum typeOf(String type) {
        return valueOf(type.toUpperCase().trim());
    }
}
