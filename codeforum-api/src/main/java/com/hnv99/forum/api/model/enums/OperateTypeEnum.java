package com.hnv99.forum.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum for operation types
 */
@Getter
@AllArgsConstructor
public enum OperateTypeEnum {

    EMPTY(0, "") {
        @Override
        public int getDbStatCode() {
            return 0;
        }
    },
    READ(1, "Read") {
        @Override
        public int getDbStatCode() {
            return ReadStatEnum.READ.getCode();
        }
    },
    PRAISE(2, "Praise") {
        @Override
        public int getDbStatCode() {
            return PraiseStatEnum.PRAISE.getCode();
        }
    },
    COLLECTION(3, "Collection") {
        @Override
        public int getDbStatCode() {
            return CollectionStatEnum.COLLECTION.getCode();
        }
    },
    CANCEL_PRAISE(4, "Cancel Praise") {
        @Override
        public int getDbStatCode() {
            return PraiseStatEnum.CANCEL_PRAISE.getCode();
        }
    },
    CANCEL_COLLECTION(5, "Cancel Collection") {
        @Override
        public int getDbStatCode() {
            return CollectionStatEnum.CANCEL_COLLECTION.getCode();
        }
    },
    COMMENT(6, "Comment") {
        @Override
        public int getDbStatCode() {
            return CommentStatEnum.COMMENT.getCode();
        }
    },
    DELETE_COMMENT(7, "Delete Comment") {
        @Override
        public int getDbStatCode() {
            return CommentStatEnum.DELETE_COMMENT.getCode();
        }
    },
    ;

    private final Integer code;
    private final String desc;

    public static OperateTypeEnum fromCode(Integer code) {
        for (OperateTypeEnum value : OperateTypeEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return OperateTypeEnum.EMPTY;
    }

    public abstract int getDbStatCode();

    /**
     * Determine if the operation is on an article or a comment.
     *
     * @param type
     * @return true if the operation is related to an article, false if related to a comment.
     */
    public static DocumentTypeEnum getOperateDocumentType(OperateTypeEnum type) {
        return (type == COMMENT || type == DELETE_COMMENT) ? DocumentTypeEnum.COMMENT : DocumentTypeEnum.ARTICLE;
    }

    /**
     * Get the corresponding notification type for the operation type.
     *
     * @param type
     * @return Corresponding notification type
     */
    public static NotifyTypeEnum getNotifyType(OperateTypeEnum type) {
        switch (type) {
            case PRAISE:
                return NotifyTypeEnum.PRAISE;
            case CANCEL_PRAISE:
                return NotifyTypeEnum.CANCEL_PRAISE;
            case COLLECTION:
                return NotifyTypeEnum.COLLECT;
            case CANCEL_COLLECTION:
                return NotifyTypeEnum.CANCEL_COLLECT;
            default:
                return null;
        }
    }
}
