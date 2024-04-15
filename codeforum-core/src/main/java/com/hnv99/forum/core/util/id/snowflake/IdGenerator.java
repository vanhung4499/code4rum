package com.hnv99.forum.core.util.id.snowflake;

public interface IdGenerator {
    /**
     * Generate distributed id
     *
     * @return
     */
    Long nextId();
}
