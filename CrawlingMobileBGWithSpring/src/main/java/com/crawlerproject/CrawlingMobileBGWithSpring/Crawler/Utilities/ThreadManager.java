package com.crawlerproject.CrawlingMobileBGWithSpring.Crawler.Utilities;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadManager {
    public static final int THREAD_COUNT = 5;

    private static final ExecutorService executor;

    static {
        executor = Executors.newFixedThreadPool(THREAD_COUNT);
    }

    private ThreadManager() {
    }

    public static ThreadManager getInstance() {
        return ThreadManagerHolder.INSTANCE;
    }

    public void executeTask(Runnable task) {
        executor.execute(task);
    }

    public static void shutdown() {
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class ThreadManagerHolder {
        private static final ThreadManager INSTANCE = new ThreadManager();
    }
}
