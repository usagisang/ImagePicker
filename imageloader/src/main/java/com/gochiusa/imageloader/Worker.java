package com.gochiusa.imageloader;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;


class Worker implements Runnable {

    /**
     *  如果没有任何一个RequestHandler可以处理Action，则使用这个。
     */
    private static final RequestHandler ERROR_HANDLER = new RequestHandler() {
        @Override
        public boolean canHandleRequest(Action data) {
            return true;
        }
        @Override
        public Bitmap load(Action data) {
            throw new IllegalStateException("未知类型的请求：" + data.getUri());
        }
    };

    private final Action action;
    private Cache mMemoryCache;
    private final Dispatcher dispatcher;
    private Bitmap result;
    Future<?> mFuture;
    boolean mSkipCache;
    String mKey;
    RequestHandler mRequestHandler;
    final ImageLoader imageLoader;

    /**
     * Worker任务被创建的时间
     */
    final long createTime = System.currentTimeMillis();

    Worker(Action action, Cache memoryCache, Dispatcher dispatcher,
           RequestHandler requestHandler, ImageLoader loader) {
        this.action = action;
        mMemoryCache = memoryCache;
        this.dispatcher = dispatcher;
        mKey = action.key;
        mSkipCache = action.skipMemoryCache || action.skipAllCache;
        mRequestHandler = requestHandler;
        imageLoader = loader;
    }

    @Override
    public void run() {
        // 在LIFO模式，检查一遍请求是否已经被取消
        if (imageLoader.LIFO && action.isCancelled()) {
            return;
        }
        try {
            result = loadBitmap();
            dispatcher.dispatchComplete(this);
        } catch (Exception ignored) {
            dispatcher.dispatchError(this);
        }
    }

    private Bitmap loadBitmap() throws IOException {
        Bitmap bitmap;
        if (! mSkipCache) {
            bitmap = mMemoryCache.get(getKey());
            if (bitmap != null) {
                return bitmap;
            }
        }
        bitmap = mRequestHandler.load(action);
        return transformResult(action, bitmap);
    }

    void cancel() {
        mFuture.cancel(false);
    }

    boolean isCancel() {
        return mFuture != null && mFuture.isCancelled();
    }

    Bitmap getResult() {
        return result;
    }

    Action getAction() {
        return action;
    }

    /**
     *  根据Action请求，创建一个对应的工作线程进行处理
     * @param action 请求包装类
     * @param memoryCache 内存缓存
     * @param dispatcher 线程调度器
     */
    static Worker forResult(Action action, Cache memoryCache, Dispatcher dispatcher) {
        List<RequestHandler> handlerList = action.getImageLoader().requestHandlers;
        int total = handlerList.size();
        // 遍历寻找能够处理这个Action的处理器
        for (int i = 0; i < total; i ++) {
            RequestHandler requestHandler = handlerList.get(i);
            if (requestHandler.canHandleRequest(action)) {
                return new Worker(action, memoryCache, dispatcher, requestHandler,
                        dispatcher.mImageLoader);
            }
        }
        return new Worker(action, memoryCache, dispatcher, ERROR_HANDLER,
                dispatcher.mImageLoader);
    }

    String getKey() {
        return mKey;
    }

    boolean isSkipCache() {
        return mSkipCache;
    }

    /**
     *  将获取的位图进行进行变换
     * @param action 储存位图需要如何变换的信息
     * @param result 需要变换的位图
     * @return 变换后的位图
     */
    static Bitmap transformResult(Action action, Bitmap result) {
        // 原图大小
        int originWidth = result.getWidth();
        int originHeight = result.getHeight();
        // 绘制的起点的坐标
        int drawX = 0;
        int drawY = 0;
        // 绘制的区域的大小，暂时设置为与原图一样大
        int drawWidth = originWidth;
        int drawHeight = originHeight;
        // 目标宽高
        int targetWidth = action.targetWidth;
        int targetHeight = action.targetHeight;

        Matrix matrix = new Matrix();
        // 如果没有合法的目标宽高，或者图片已经等于目标宽高，返回原图
        if ((! action.hasSize()) || (originHeight == targetHeight && originWidth == targetWidth)) {
            return result;
        }
        // 目标与源图之间的比例
        float widthRatio = targetWidth / (float) originWidth;
        float heightRatio = targetHeight / (float) originHeight;
        // 最终缩放比例
        float scale;
        if (action.centerCrop) {
            // 如果是CenterCrop模式，截取后缩小。比例更小的说明其相对更长
            if (widthRatio > heightRatio) {
                // 用较大的比例缩放
                scale = widthRatio;
                // 如果是高相对更长，截取高度，以原宽为基础，计算符合目标的宽高比例的新高
                // 这用来定位从何处开始截取，以及到底截取多少长度
                // 计算式等价于originWidth *(targetHeight / targetWidth)，为了float的精度而没有这样写
                int cropHeight = (int) Math.ceil(originHeight * (heightRatio / widthRatio));
                // 计算绘制纵坐标
                drawY = (originHeight - cropHeight) / 2;
                drawHeight = cropHeight;
            } else {
                scale = heightRatio;
                int cropWidth = (int) Math.ceil(originWidth * (widthRatio / heightRatio));
                drawX = (originWidth - cropWidth) / 2;
                drawWidth = cropWidth;
            }
            matrix.preScale(scale, scale);
        } else if (action.centerInside) {
            // 如果是CenterInside模式，只需要最小的比例就行了
            scale = Math.min(widthRatio, heightRatio);
            matrix.preScale(scale, scale);
        } else if (targetHeight != 0 || targetWidth != 0) {
            // 如果目标宽度为0，采用高度的方案
            float sx = targetWidth != 0 ?
                    targetWidth / (float) originWidth : targetHeight / (float) originHeight;
            // 如果目标高度为0，采用宽度的方案
            float sy = targetHeight != 0 ?
                    targetHeight / (float) originHeight : targetWidth / (float) originWidth;
            matrix.preScale(sx, sy);
        }
        // 创建新的位图
        Bitmap newResult =
                Bitmap.createBitmap(result, drawX, drawY, drawWidth, drawHeight, matrix, true);
        if (newResult != result) {
            // 回收位图
            result.recycle();
            result = newResult;
        }
        return result;
    }


}
