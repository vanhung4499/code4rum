package com.hnv99.forum.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum for user page selection
 */
@Getter
@AllArgsConstructor
public enum HomeSelectEnum {

    ARTICLE("article", "Articles"),
    READ("read", "Reading History"),
    FOLLOW("follow", "Following"),
    COLLECTION("collection", "Collections");

    private final String code;
    private final String desc;

    public static HomeSelectEnum fromCode(String code) {
        for (HomeSelectEnum value : HomeSelectEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return HomeSelectEnum.ARTICLE;
    }
}
