package com.hnv99.forum.core.util.id.snowflake;

import com.hnv99.forum.core.async.AsyncUtil;
import com.hnv99.forum.core.util.DateUtil;
import com.hnv99.forum.core.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import org.apache.commons.lang3.StringUtils;

/**
 * Custom implementation of the Snowflake algorithm-based ID generator.
 * <p>
 * Time + Data Center (3 bits) + Worker ID (7 bits) + Sequence Number (12 bits)
 */
@Slf4j
public class PaiSnowflakeIdGenerator implements IdGenerator {
    /**
     * Number of bits for the incrementing sequence
     */
    private static final long SEQUENCE_BITS = 10L;

    /**
     * Number of bits for the worker ID
     */
    private static final long WORKER_ID_BITS = 7L;
    private static final long DATA_CENTER_BITS = 3L;

    private static final long SEQUENCE_MASK = (1 << SEQUENCE_BITS) - 1;

    private static final long WORKER_ID_LEFT_SHIFT_BITS = SEQUENCE_BITS;
    private static final long DATACENTER_LEFT_SHIFT_BITS = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_LEFT_SHIFT_BITS = WORKER_ID_LEFT_SHIFT_BITS + WORKER_ID_BITS + DATA_CENTER_BITS;

    /**
     * Worker ID (7 bits)
     */
    private long workId = 1;
    /**
     * Data Center (3 bits)
     */
    private long dataCenter = 1;

    /**
     * Last accessed time
     */
    private long lastTime;
    /**
     * Incrementing sequence
     */
    private long sequence;

    private byte sequenceOffset;

    public PaiSnowflakeIdGenerator() {
        try {
            String ip = IpUtil.getLocalIp4Address();
            String[] cells = StringUtils.split(ip, ".");
            this.dataCenter = Integer.parseInt(cells[0]) & ((1 << DATA_CENTER_BITS) - 1);
            this.workId = Integer.parseInt(cells[3]) >> 16 & ((1 << WORKER_ID_BITS) - 1);
        } catch (Exception e) {
            this.dataCenter = 1;
            this.workId = 1;
        }
    }

    public PaiSnowflakeIdGenerator(int workId, int dateCenter) {
        this.workId = workId;
        this.dataCenter = dateCenter;
    }

    /**
     * Generate an ID with trend-incrementing sequence.
     *
     * @return
     */
    @Override
    public synchronized Long nextId() {
        long nowTime = waitToIncrDiffIfNeed(getNowTime());
        if (lastTime == nowTime) {
            if (0L == (sequence = (sequence + 1) & SEQUENCE_MASK)) {
                // Indicates that the incrementing number for this moment is used up; wait for the next time point
                nowTime = waitUntilNextTime(nowTime);
            }
        } else {
            // If 0 is the starting value for the sequence in the last millisecond, then use 1 as the starting value for this millisecond
            vibrateSequenceOffset();
            sequence = sequenceOffset;
        }
        lastTime = nowTime;
        long ans = ((nowTime % DateUtil.ONE_DAY_SECONDS) << TIMESTAMP_LEFT_SHIFT_BITS) | (dataCenter << DATACENTER_LEFT_SHIFT_BITS) | (workId << WORKER_ID_LEFT_SHIFT_BITS) | sequence;
        if (log.isDebugEnabled()) {
            log.debug("seconds:{}, datacenter:{}, work:{}, seq:{}, ans={}", nowTime % DateUtil.ONE_DAY_SECONDS, dataCenter, workId, sequence, ans);
        }
        return Long.parseLong(String.format("%s%011d", getDaySegment(nowTime), ans));
    }

    /**
     * If the current time is smaller than the last execution time, wait until the time catches up to avoid duplicate data due to clock rollback.
     *
     * @param nowTime Current timestamp
     * @return New timestamp
     */
    private long waitToIncrDiffIfNeed(final long nowTime) {
        if (lastTime <= nowTime) {
            return nowTime;
        }
        long diff = lastTime - nowTime;
        AsyncUtil.sleep(diff);
        return getNowTime();
    }

    /**
     * Wait until the next second.
     *
     * @param lastTime
     * @return
     */
    private long waitUntilNextTime(final long lastTime) {
        long result = getNowTime();
        while (result <= lastTime) {
            result = getNowTime();
        }
        return result;
    }

    private void vibrateSequenceOffset() {
        sequenceOffset = (byte) (~sequenceOffset & 1);
    }


    /**
     * Get the current time.
     *
     * @return Seconds
     */
    private long getNowTime() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * Build partition based on year, month, and day.
     *
     * @param time Timestamp
     * @return Time segment
     */
    private static String getDaySegment(long time) {
        LocalDateTime localDate = DateUtil.time2LocalTime(time * 1000L);
        return String.format("%02d%03d", localDate.getYear() % 100, localDate.getDayOfYear());
    }
}
