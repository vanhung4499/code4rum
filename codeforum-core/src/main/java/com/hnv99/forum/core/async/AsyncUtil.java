package com.hnv99.forum.core.async;

import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.core.util.ArrayUtil;
import com.hnv99.forum.core.util.EnvUtil;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import lombok.extern.slf4j.Slf4j;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
/**
 * Asynchronous utility class
 */
@Slf4j
public class AsyncUtil {
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        private final ThreadFactory defaultFactory = Executors.defaultThreadFactory();
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            Thread thread = this.defaultFactory.newThread(r);
            if (!thread.isDaemon()) {
                thread.setDaemon(true);
            }

            thread.setName("codeforum-" + this.threadNumber.getAndIncrement());
            return thread;
        }
    };
    private static ExecutorService executorService;
    private static SimpleTimeLimiter simpleTimeLimiter;

    static {
        initExecutorService(Runtime.getRuntime().availableProcessors() * 2, 50);
    }

    public static void initExecutorService(int core, int max) {
        // Default thread pool construction for asynchronous utility classes, parameter selection principles:
        //   1. The technology faction does not have CPU-intensive tasks, and most operations involve IO operations such as redis/mysql
        //   2. This thread pool is a common execution repository for unified asynchronous encapsulation tools, and it is not expected to be affected by other thread executions. Therefore, the queue length is 0, the core thread is full, and the thread is directly executed by the current thread when it exceeds the maximum thread
        //   3. Similarly, because it belongs to a general-purpose utility class, combined with the actual situation of asynchronous use of technology factions, it is actually not very saturated, so idle threads can be directly recycled; in most scenarios, cpu * 2 threads are sufficient
        max = Math.max(core, max);
        executorService = new ExecutorBuilder()
                .setCorePoolSize(core)
                .setMaxPoolSize(max)
                .setKeepAliveTime(0)
                .setKeepAliveTime(0, TimeUnit.SECONDS)
                .setWorkQueue(new SynchronousQueue<Runnable>())
                .setHandler(new ThreadPoolExecutor.CallerRunsPolicy())
                .setThreadFactory(THREAD_FACTORY)
                .buildFinalizable();
        simpleTimeLimiter = SimpleTimeLimiter.create(executorService);
    }


    /**
     * Method call execution with timeout, if the execution time exceeds the given time, a timeout exception is returned, and the internal task is still executed normally
     * If the execution is completed within the timeout period, it returns directly
     *
     * @param time
     * @param unit
     * @param call
     * @param <T>
     * @return
     */
    public static <T> T callWithTimeLimit(long time, TimeUnit unit, Callable<T> call) throws ExecutionException, InterruptedException, TimeoutException {
        return simpleTimeLimiter.callWithTimeout(call, time, unit);
    }


    public static void execute(Runnable call) {
        executorService.execute(call);
    }

    public static <T> Future<T> submit(Callable<T> t) {
        return executorService.submit(t);
    }


    public static boolean sleep(Number timeout, TimeUnit timeUnit) {
        try {
            timeUnit.sleep(timeout.longValue());
            return true;
        } catch (InterruptedException var3) {
            return false;
        }
    }

    public static boolean sleep(Number millis) {
        return millis == null ? true : sleep(millis.longValue());
    }

    public static boolean sleep(long millis) {
        if (millis > 0L) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException var3) {
                return false;
            }
        }

        return true;
    }


    public static class CompletableFutureBridge {
        private List<CompletableFuture> list;
        private Map<String, Long> cost;
        private String taskName;

        public CompletableFutureBridge() {
            this("CompletableFutureExecute");
        }

        public CompletableFutureBridge(String task) {
            this.taskName = task;
            list = new ArrayList<>();
            cost = new ConcurrentHashMap<>();
            cost.put(task, System.currentTimeMillis());
        }

        /**
         * Asynchronous execution with a return result
         *
         * @param supplier
         * @return
         */
        public CompletableFutureBridge supplyAsync(Supplier supplier) {
            return supplyAsync(supplier, executorService);
        }

        public CompletableFutureBridge supplyAsync(Supplier supplier, ExecutorService executor) {
            return supplyAsyncWithTimeRecord(supplier, supplier.toString(), executor);
        }

        public CompletableFutureBridge supplyAsyncWithTimeRecord(Supplier supplier, String name) {
            return supplyAsyncWithTimeRecord(supplier, name, executorService);
        }

        public CompletableFutureBridge supplyAsyncWithTimeRecord(Supplier supplier, String name, ExecutorService executor) {
            list.add(CompletableFuture.supplyAsync(supplyWithTime(supplier, name), executor));
            return this;
        }


        /**
         * Asynchronous concurrent execution without a return result
         *
         * @param run
         * @return
         */
        public CompletableFutureBridge runAsync(Runnable run) {
            list.add(CompletableFuture.runAsync(runWithTime(run, run.toString()), executorService));
            return this;
        }

        public CompletableFutureBridge runAsync(Runnable run, ExecutorService executor) {
            return runAsyncWithTimeRecord(run, run.toString(), executor);
        }


        /**
         * Asynchronous concurrent execution with time recording
         *
         * @param run
         * @param name
         * @return
         */
        public CompletableFutureBridge runAsyncWithTimeRecord(Runnable run, String name) {
            return runAsyncWithTimeRecord(run, name, executorService);
        }

        public CompletableFutureBridge runAsyncWithTimeRecord(Runnable run, String name, ExecutorService executor) {
            list.add(CompletableFuture.runAsync(runWithTime(run, name), executor));
            return this;
        }

        private Runnable runWithTime(Runnable run, String name) {
            return () -> {
                startRecord(name);
                try {
                    run.run();
                } finally {
                    endRecord(name);
                }
            };
        }

        private Supplier supplyWithTime(Supplier call, String name) {
            return () -> {
                startRecord(name);
                try {
                    return call.get();
                } finally {
                    endRecord(name);
                }
            };
        }

        public CompletableFutureBridge allExecuted() {
            CompletableFuture.allOf(ArrayUtil.toArray(list, CompletableFuture.class)).join();
            endRecord(this.taskName);
            return this;
        }

        private void startRecord(String name) {
            cost.put(name, System.currentTimeMillis());
        }

        private void endRecord(String name) {
            long now = System.currentTimeMillis();
            cost.put(name, now - cost.getOrDefault(name, now));
        }

        public void prettyPrint() {
            StringBuilder sb = new StringBuilder();
            sb.append('\n');
            long totalCost = cost.remove(taskName);
            sb.append("StopWatch '").append(taskName).append("': running time = ").append(totalCost).append(" ms");
            sb.append('\n');
            if (cost.size() <= 1) {
                sb.append("No task info kept");
            } else {
                sb.append("---------------------------------------------\n");
                sb.append("ms         %     Task name\n");
                sb.append("---------------------------------------------\n");
                NumberFormat pf = NumberFormat.getPercentInstance();
                pf.setMinimumIntegerDigits(2);
                pf.setMinimumFractionDigits(2);
                pf.setGroupingUsed(false);
                for (Map.Entry<String, Long> entry : cost.entrySet()) {
                    sb.append(entry.getValue()).append("\t\t");
                    sb.append(pf.format(entry.getValue() / (double) totalCost)).append("\t\t");
                    sb.append(entry.getKey()).append("\n");
                }
            }
            if (!EnvUtil.isPro()) {
                log.info("\n---------------------\n{}\n--------------------\n", sb);
            }
        }
    }

    public static CompletableFutureBridge concurrentExecutor(String... name) {
        if (name.length > 0) {
            return new CompletableFutureBridge(name[0]);
        }
        return new CompletableFutureBridge();
    }
}
