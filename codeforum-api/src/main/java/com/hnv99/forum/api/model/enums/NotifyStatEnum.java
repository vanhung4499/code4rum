package com.hnv99.forum.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum for notification status
 */
@Getter
@AllArgsConstructor
public enum NotifyStatEnum {
    UNREAD(0, "Unread"),
    READ(1, "Read");

    private int stat;
    private String msg;
}
