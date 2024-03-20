package com.at.rpc.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class ThreadUtils {

    private static final AtomicLong atomicLong = new AtomicLong(0);

    public static ScheduledExecutorService newDaemonSingleThreadScheduledExecutor(String threadName) {

        ThreadFactory threadFactory = createThreadFactory(threadName);
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, threadFactory);
        // By default, a cancelled task is not automatically removed from the work queue until its delay
        // elapses. We have to enable it manually.
        executor.setRemoveOnCancelPolicy(true);
        return executor;
    }

    private static ThreadFactory createThreadFactory(String threadName) {
        return new ThreadFactoryBuilder()
                .setNameFormat(String.format("%s-%d", threadName, atomicLong.getAndIncrement()))
                .setDaemon(true)
                .setUncaughtExceptionHandler(new ThreadUncaughtExceptionHandler())
                .build();
    }

}
