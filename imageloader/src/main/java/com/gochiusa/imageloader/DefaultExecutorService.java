package com.gochiusa.imageloader;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DefaultExecutorService extends ThreadPoolExecutor {
    /**
     *  默认的线程池线程数
     */
    public static final int DEFAULT_THREAD_COUNT = 3;

    public DefaultExecutorService(boolean LIFO) {
        super(DEFAULT_THREAD_COUNT, DEFAULT_THREAD_COUNT, 0L, TimeUnit.MILLISECONDS,
                Utils.createQueue(LIFO));
    }
    public DefaultExecutorService(int threadCount, boolean LIFO) {
        super(threadCount, threadCount, 0L, TimeUnit.MILLISECONDS,
                Utils.createQueue(LIFO));
    }
}
