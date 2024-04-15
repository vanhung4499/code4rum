package com.hnv99.forum.core.util.id.snowflake;

import com.hnv99.forum.core.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * ID generator based on Snowflake algorithm.
 */
@Slf4j
public class SnowflakeProducer {
    private BlockingQueue<Long> queue;

    /**
     * Interval time for ID expiration.
     */
    public static final Long ID_EXPIRE_TIME_INTER = DateUtil.ONE_DAY_MILL;
    private static final int QUEUE_SIZE = 10;
    private ExecutorService es = Executors.newSingleThreadExecutor((Runnable r) -> {
        Thread t = new Thread(r);
        t.setName("SnowflakeProducer-generate-thread");
        t.setDaemon(true);
        return t;
    });

    public SnowflakeProducer(final IdGenerator generator) {
        queue = new LinkedBlockingQueue<>(QUEUE_SIZE);
        es.submit(() -> {
            long lastTime = System.currentTimeMillis();
            while (true) {
                try {
                    queue.offer(generator.nextId(), 1, TimeUnit.MINUTES);
                } catch (InterruptedException e1) {
                } catch (Exception e) {
                    log.info("Error generating ID! {}", e.getMessage());
                }

                // Automatically reset business ID when crossing days
                try {
                    long now = System.currentTimeMillis();
                    if (now / ID_EXPIRE_TIME_INTER - lastTime / ID_EXPIRE_TIME_INTER > 0) {
                        // Crossed day, clear the queue
                        queue.clear();
                        log.info("Clearing the ID queue and resetting.");
                    }
                    lastTime = now;

                } catch (Exception e) {
                    log.info("Error auto removing illegal IDs! {}", e.getMessage());
                }
            }
        });
    }

    public Long genId() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            log.error("Exception in Snowflake algorithm generation logic", e);
            throw new RuntimeException("Exception in Snowflake algorithm ID generation!", e);
        }
    }
}
