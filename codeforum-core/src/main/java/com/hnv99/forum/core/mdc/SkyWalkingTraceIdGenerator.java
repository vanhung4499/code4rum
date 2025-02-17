package com.hnv99.forum.core.mdc;

import com.google.common.base.Joiner;

import java.util.UUID;

/**
 * TraceId generation strategy for SkyWalking.
 * <p>
 * Source code: <a href="https://github.com/apache/skywalking-java/blob/ddc68e27e2764ca6299f04ef21a5d864bf660deb/apm-sniffer/apm-agent-core/src/main/java/org/apache/skywalking/apm/agent/core/context/ids/GlobalIdGenerator.java"/>
 */
public class SkyWalkingTraceIdGenerator {
    private static final String PROCESS_ID = UUID.randomUUID().toString().replaceAll("-", "");
    private static final ThreadLocal<IDContext> THREAD_ID_SEQUENCE = ThreadLocal.withInitial(
            () -> new IDContext(System.currentTimeMillis(), (short) 0));

    private SkyWalkingTraceIdGenerator() {
    }

    /**
     * Generate a new id, combined by three parts.
     * <p>
     * The first one represents the application instance id.
     * <p>
     * The second one represents the thread id.
     * <p>
     * The third one also has two parts: 1) a timestamp, measured in milliseconds, 2) a sequence number, in the current thread, between
     * 0 (inclusive) and 9999 (inclusive).
     *
     * @return unique id to represent a trace or segment
     */
    public static String generate() {
        return Joiner.on(".").join(
                PROCESS_ID,
                String.valueOf(Thread.currentThread().getId()),
                String.valueOf(THREAD_ID_SEQUENCE.get().nextSeq())
        );
    }

    private static class IDContext {
        private static final int MAX_SEQ = 10_000;
        private long lastTimestamp;
        private short threadSeq;

        // Just for considering time-shift-back only.
        private long lastShiftTimestamp;
        private int lastShiftValue;

        private IDContext(long lastTimestamp, short threadSeq) {
            this.lastTimestamp = lastTimestamp;
            this.threadSeq = threadSeq;
        }

        private long nextSeq() {
            return timestamp() * 10000 + nextThreadSeq();
        }

        private long timestamp() {
            long currentTimeMillis = System.currentTimeMillis();

            if (currentTimeMillis < lastTimestamp) {
                // Just for considering time-shift-back by Ops or OS.
                if (lastShiftTimestamp != currentTimeMillis) {
                    lastShiftValue++;
                    lastShiftTimestamp = currentTimeMillis;
                }
                return lastShiftValue;
            } else {
                lastTimestamp = currentTimeMillis;
                return lastTimestamp;
            }
        }

        private short nextThreadSeq() {
            if (threadSeq == MAX_SEQ) {
                threadSeq = 0;
            }
            return threadSeq++;
        }
    }
}

