package com.hnv99.forum.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum for article operations
 */
@Getter
@AllArgsConstructor
public enum OperateArticleEnum {

    EMPTY(0, "") {
        @Override
        public int getDbStatCode() {
            return 0;
        }
    },
    OFFICIAL(1, "Official") {
        @Override
        public int getDbStatCode() {
            return OfficialStatEnum.OFFICIAL.getCode();
        }
    },
    CANCEL_OFFICIAL(2, "Non-official"){
        @Override
        public int getDbStatCode() {
            return OfficialStatEnum.NOT_OFFICIAL.getCode();
        }
    },
    TOPPING(3, "Topping"){
        @Override
        public int getDbStatCode() {
            return ToppingStatEnum.TOPPING.getCode();
        }
    },
    CANCEL_TOPPING(4, "Not Topping"){
        @Override
        public int getDbStatCode() {
            return ToppingStatEnum.NOT_TOPPING.getCode();
        }
    },
    CREAM(5, "Featured"){
        @Override
        public int getDbStatCode() {
            return CreamStatEnum.CREAM.getCode();
        }
    },
    CANCEL_CREAM(6, "Not Featured"){
        @Override
        public int getDbStatCode() {
            return CreamStatEnum.NOT_CREAM.getCode();
        }
    };

    private final Integer code;
    private final String desc;

    public static OperateArticleEnum fromCode(Integer code) {
        for (OperateArticleEnum value : OperateArticleEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return OperateArticleEnum.OFFICIAL;
    }

    public abstract int getDbStatCode();
}
