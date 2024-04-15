package com.hnv99.forum.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enumeration of article types.
 */
@Getter
@AllArgsConstructor
public enum ArticleTypeEnum {

    EMPTY(0, ""),
    BLOG(1, "Blog"),
    ANSWER(2, "Answer"),
    COLUMN(3, "Column"),
    ;

    private final Integer code;
    private final String desc;

    public static ArticleTypeEnum fromCode(Integer code) {
        for (ArticleTypeEnum value : ArticleTypeEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return ArticleTypeEnum.EMPTY;
    }
}
