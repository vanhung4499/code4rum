package com.hnv99.forum.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum for notification types
 */
@Getter
@AllArgsConstructor
public enum NotifyTypeEnum {
    COMMENT(1, "Comment"),
    REPLY(2, "Reply"),
    PRAISE(3, "Praise"),
    COLLECT(4, "Collect"),
    FOLLOW(5, "Follow Notification"),
    SYSTEM(6, "System Notification"),
    DELETE_COMMENT(1, "Delete Comment"),
    DELETE_REPLY(2, "Delete Reply"),
    CANCEL_PRAISE(3, "Cancel Praise"),
    CANCEL_COLLECT(4, "Cancel Collect"),
    CANCEL_FOLLOW(5, "Cancel Follow"),

    // Registration and login related system prompts
    REGISTER(6, "User Registration"),
    BIND(6, "Bind Star"),
    LOGIN(6, "User Login"),
    ;

    private int type;
    private String msg;

    private static Map<Integer, NotifyTypeEnum> mapper;

    static {
        mapper = new HashMap<>();
        for (NotifyTypeEnum type : values()) {
            mapper.put(type.type, type);
        }
    }

    public static NotifyTypeEnum typeOf(int type) {
        return mapper.get(type);
    }

    public static NotifyTypeEnum typeOf(String type) {
        return valueOf(type.toUpperCase().trim());
    }
}
