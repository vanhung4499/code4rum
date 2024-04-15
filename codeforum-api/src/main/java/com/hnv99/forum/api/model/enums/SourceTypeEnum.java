package com.hnv99.forum.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum for document sources
 */
@Getter
@AllArgsConstructor
public enum SourceTypeEnum {

    EMPTY(0, ""),
    REPRINT(1, "Reprint"),
    ORIGINAL(2, "Original"),
    TRANSLATION(3, "Translation");

    private final Integer code;
    private final String desc;

    public static SourceTypeEnum fromCode(Integer code) {
        for (SourceTypeEnum value : SourceTypeEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return SourceTypeEnum.EMPTY;
    }
}
