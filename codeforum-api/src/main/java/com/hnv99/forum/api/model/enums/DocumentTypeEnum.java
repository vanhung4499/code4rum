package com.hnv99.forum.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum for document types
 */
@Getter
@AllArgsConstructor
public enum DocumentTypeEnum {

    EMPTY(0, ""),
    ARTICLE(1, "Article"),
    COMMENT(2, "Comment");

    private final Integer code;
    private final String desc;

    public static DocumentTypeEnum fromCode(Integer code) {
        for (DocumentTypeEnum value : DocumentTypeEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return DocumentTypeEnum.EMPTY;
    }
}

