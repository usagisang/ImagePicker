package com.gochiusa.imageloader;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

class Dispatcher {

    /**
     *  分发线程的名称
     */
    private static final String DISPATCH_THREAD_NAME = "dispatchThread";

    private static final int REQUEST_SUBMIT = 1;
    private static final int REQUEST_CANCEL = 2;
    private static final int WORKER_COMPLETE = 3;
    private static final int WORKER_ERROR = 4;
    private static final int WORKER_DELAY_NEXT_BATCH = 5;

    /**
     *  批处理消息延时，单位是ms
     */
    private static final int BATCH_DELAY = 200;
    /**
     *  返回批处理的Worker的标志
     */
    public static final int WORKER_BATCH_COMPLETE = 30;


    private Map<String, Worker> mWorkerMap;
    private Downloader mDownloader;
    private ExecutorService mExecutorService;
    private Handler mMainHandler;
    /**
     *  分发处理线程
     */
    private DispatchThread mDispatchThread;
    private Cache mMemoryCache;
    /**
     *  与分发处理线程绑定的Handler
     */
    private Handler mDispatchHandler;

    private List<Worker> mBatch;


    Dispatcher(Downloader downloader, ExecutorService executorService,
               Cache memoryCache, Handler mainHandler) {
        mWorkerMap = new HashMap<>();
        this.mDownloader = downloader;
        this.mExecutorService = executorService;
        mMainHandler = mainHandler;
        mDispatchThread = new DispatchThread();
        // 让线程开始工作
        mDispatchThread.start();
        mMemoryCache = memoryCache;
        mDispatchHandler = new DispatchHandler(mDispatchThread.getLooper(), this);
        mBatch = new ArrayList<>();
    }

    void shutdown() {
        mExecutorService.shutdown();
        mDispatchThread.quit();
        mDownloader.shutdown();
    }

    void performCancel(Action action) {
        Worker worker= mWorkerMap.remove(action.key);
        if (worker != null) {
            // 取消worker的加载
            worker.cancel();
        }
    }

    /**
     *  提交Action到子线程执行加载任务
     */
    void performSubmit(Action action) {
        if (mExecutorService.isShutdown()) {
            return;
        }
        Worker worker = Worker.forResult(action, mMemoryCache, this);
        mWorkerMap.put(worker.getKey(), worker);
        worker.mFuture = mExecutorService.submit(worker);
    }

    /**
     *  一个Worker线程加载图片完毕之后，将会使用这个方法处理
     * @param worker 加载图片完毕的Worker线程
     */
    void performWorkerComplete(Worker worker) {
        // 如果不跳过缓存
        if (! worker.mSkipCache) {
            mMemoryCache.set(worker.getKey(), worker.getResult());
        }
        mWorkerMap.remove(worker.getKey());
        // 执行批处理操作
        batch(worker);
    }

    void performError(Worker worker) {
        mWorkerMap.remove(worker.getKey());
        batch(worker);
    }

    /**
     *  批处理操作，将Worker缓存到批处理列表，并尝试发送一个空的延迟消息
     * @param worker 待执行批处理的Worker
     */
    private void batch(Worker worker) {
        // 过滤掉已经被标记为取消的Worker，因为取消一个正在运行的线程不一定成功
        if (worker.isCancel()) {
            return;
        }
        mBatch.add(worker);
        // 检查是否已经存在延迟消息，如果不存在则发送
        if (! mDispatchHandler.hasMessages(WORKER_DELAY_NEXT_BATCH)) {
            mDispatchHandler.sendEmptyMessageDelayed(WORKER_DELAY_NEXT_BATCH, BATCH_DELAY);
        }
    }

    /**
     *  批处理列表准备完毕的处理
     */
    void performBatchComplete() {
        List<Worker> copy = new ArrayList<>(mBatch);
        // 清空批处理列表
        mBatch.clear();
        // 向主线程发送批处理列表
        mMainHandler.sendMessage(mMainHandler.obtainMessage(WORKER_BATCH_COMPLETE, copy));
    }

    void dispatchComplete(Worker worker) {
        mDispatchHandler.sendMessage(mDispatchHandler.obtainMessage(WORKER_COMPLETE, worker));
    }


    void dispatchError(Worker worker) {
        mDispatchHandler.sendMessage(mDispatchHandler.obtainMessage(WORKER_ERROR, worker));
    }

    void dispatchSubmit(Action action) {
        mDispatchHandler.sendMessage(mDispatchHandler.obtainMessage(REQUEST_SUBMIT, action));
    }
    void dispatchCancel(Action action) {
        mDispatchHandler.sendMessage(mDispatchHandler.obtainMessage(REQUEST_CANCEL, action));
    }


    /**
     *  分发处理线程
     */
    static class DispatchThread extends HandlerThread {

        public DispatchThread() {
            super(DISPATCH_THREAD_NAME, android.os.Process.THREAD_PRIORITY_BACKGROUND);
        }
    }

    /**
     *  分发Message到处理线程的Handler
     */
    private static class DispatchHandler extends Handler {
        private final Dispatcher dispatcher;

        public DispatchHandler(Looper looper, Dispatcher dispatcher) {
            super(looper);
            this.dispatcher = dispatcher;
        }

        @Override
        public void handleMessage(@NonNull Message message) {
            switch (message.what) {
                case REQUEST_SUBMIT : {
                    Action action = (Action) message.obj;
                    dispatcher.performSubmit(action);
                    break;
                }
                case REQUEST_CANCEL : {
                    Action action = (Action) message.obj;
                    dispatcher.performCancel(action);
                    break;
                }
                case WORKER_COMPLETE : {
                    Worker worker = (Worker) message.obj;
                    dispatcher.performWorkerComplete(worker);
                    break;
                }
                case WORKER_ERROR : {
                    Worker worker = (Worker) message.obj;
                    dispatcher.performError(worker);
                    break;
                }
                case WORKER_DELAY_NEXT_BATCH : {
                    dispatcher.performBatchComplete();
                    break;
                }
            }
        }
    }
}
