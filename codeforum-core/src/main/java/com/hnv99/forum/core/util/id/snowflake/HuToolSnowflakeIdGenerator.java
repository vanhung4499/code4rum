package com.hnv99.forum.core.util.id.snowflake;

import cn.hutool.core.lang.Snowflake;

import java.util.Date;

/**
 * Implementation of ID generator using HuTool's Snowflake algorithm.
 */
public class HuToolSnowflakeIdGenerator implements IdGenerator {
    /**
     * The epoch time for the Snowflake algorithm.
     */
    private static final Date EPOC = new Date(2023, 1, 1);

    /**
     * The Snowflake instance.
     */
    private Snowflake snowflake;

    /**
     * Constructor for initializing the Snowflake ID generator with the specified worker ID and data center ID.
     *
     * @param workId The worker ID.
     * @param datacenter The data center ID.
     */
    public HuToolSnowflakeIdGenerator(int workId, int datacenter) {
        snowflake = new Snowflake(EPOC, workId, datacenter, false);
    }

    /**
     * Generates the next ID using the Snowflake algorithm.
     *
     * @return The generated ID.
     */
    @Override
    public Long nextId() {
        return snowflake.nextId();
    }
}

