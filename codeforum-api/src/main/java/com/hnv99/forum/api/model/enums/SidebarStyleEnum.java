package com.hnv99.forum.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum for sidebar styles
 */
@Getter
@AllArgsConstructor
public enum SidebarStyleEnum {

    NOTICE(1),
    ARTICLES(2),
    RECOMMEND(3),
    ABOUT(4),
    COLUMN(5),
    PDF(6),
    SUBSCRIBE(7),
    /**
     * Active ranking list
     */
    ACTIVITY_RANK(8);

    private int style;
}

