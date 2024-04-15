package com.hnv99.forum.core.util.id;

import com.hnv99.forum.core.async.AsyncUtil;
import com.hnv99.forum.core.util.CompressUtil;
import com.hnv99.forum.core.util.id.snowflake.PaiSnowflakeIdGenerator;
import com.hnv99.forum.core.util.id.snowflake.SnowflakeProducer;

import static com.hnv99.forum.core.util.CompressUtil.int2str;

/**
 * Utility class for generating IDs.
 */
public class IdUtil {
    /**
     * Default ID generator
     */
    public static SnowflakeProducer DEFAULT_ID_PRODUCER = new SnowflakeProducer(new PaiSnowflakeIdGenerator());

    /**
     * Generate a global ID.
     *
     * @return
     */
    public static Long genId() {
        return DEFAULT_ID_PRODUCER.genId();
    }

    /**
     * Generate a string format global ID.
     *
     * @return
     */
    public static String genStrId() {
        return int2str(genId());
    }

    public static void main(String[] args) {
        System.out.println(IdUtil.genStrId());
        Long id = IdUtil.genId();
        System.out.println(id + " = " + int2str(id));
        System.out.println(IdUtil.genId() + "->" + IdUtil.genStrId());
        AsyncUtil.sleep(2000);
        System.out.println(IdUtil.genId() + "->" + IdUtil.genStrId());

        System.out.println("-----");

        SnowflakeProducer producer = new SnowflakeProducer(new PaiSnowflakeIdGenerator());
        id = producer.genId();
        System.out.println("id: " + id + " -> " + int2str(id));
        AsyncUtil.sleep(3000L);
        id = producer.genId();
        System.out.println("id: " + id + " -> " + int2str(id));
    }
}

