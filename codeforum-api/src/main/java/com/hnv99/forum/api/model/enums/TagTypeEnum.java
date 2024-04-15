package com.hnv99.forum.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum for tag types
 */
@Getter
@AllArgsConstructor
public enum TagTypeEnum {

    SYSTEM_TAG(1, "System Tag"),
    CUSTOM_TAG(2, "Custom Tag");

    private final Integer code;
    private final String desc;

    public static TagTypeEnum fromCode(Integer code) {
        for (TagTypeEnum value : TagTypeEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return TagTypeEnum.SYSTEM_TAG;
    }
}
