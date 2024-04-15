package com.hnv99.forum.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum for top status
 */
@Getter
@AllArgsConstructor
public enum ToppingStatEnum {

    NOT_TOPPING(0, "Not Topping"),
    TOPPING(1, "Topping");

    private final Integer code;
    private final String desc;

    public static ToppingStatEnum fromCode(Integer code) {
        for (ToppingStatEnum value : ToppingStatEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return ToppingStatEnum.NOT_TOPPING;
    }
}
