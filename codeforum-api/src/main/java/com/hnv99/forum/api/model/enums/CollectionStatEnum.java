package com.hnv99.forum.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum for collection status
 */
@Getter
@AllArgsConstructor
public enum CollectionStatEnum {

    NOT_COLLECTION(0, "Not Collected"),
    COLLECTION(1, "Collected"),
    CANCEL_COLLECTION(2, "Cancel Collection");

    private final Integer code;
    private final String desc;

    public static CollectionStatEnum fromCode(Integer code) {
        for (CollectionStatEnum value : CollectionStatEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return CollectionStatEnum.NOT_COLLECTION;
    }
}
