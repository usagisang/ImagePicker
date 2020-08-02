package com.gochiusa.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;

public abstract class RequestHandler {
    /**
     * 判断这个{@link RequestHandler} 是否能够处理这个请求
     */
    public abstract boolean canHandleRequest(Action data);

    /**
     * 从给定的请求消息中加载数据
     */
    public abstract Bitmap load(Action data) throws IOException;

    /**
     * 根据{@link Action}的数据，进行懒加载{@link BitmapFactory.Options}
     * 仅当需要的时候创建
     */
    static BitmapFactory.Options createBitmapOptions(Action data) {
        final boolean justBounds = data.hasSize();
        BitmapFactory.Options options = null;
        if (justBounds) {
            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
        }
        return options;
    }

    static boolean requiresInSampleSize(BitmapFactory.Options options) {
        return options != null && options.inJustDecodeBounds;
    }

    static void calculateInSampleSize(int reqWidth, int reqHeight, BitmapFactory.Options options,
                                      Action action) {
        calculateInSampleSize(reqWidth, reqHeight, options.outWidth, options.outHeight, options,
                action);
    }

    /**
     *  根据提供的宽高信息，计算缩放比例并设置在{@code options}中
     * @param reqWidth 期望的宽度
     * @param reqHeight 期望的高度
     * @param width 目前实际宽度
     * @param height 目前实际高度
     * @param options 记录缩放比例的选项
     */
    static void calculateInSampleSize(int reqWidth, int reqHeight, int width, int height,
                                      BitmapFactory.Options options, Action action) {
        int sampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio;
            final int widthRatio;
            if (reqHeight == 0) {
                sampleSize = (int) Math.floor((float) width / (float) reqWidth);
            } else if (reqWidth == 0) {
                sampleSize = (int) Math.floor((float) height / (float) reqHeight);
            } else {
                heightRatio = (int) Math.floor((float) height / (float) reqHeight);
                widthRatio = (int) Math.floor((float) width / (float) reqWidth);
                sampleSize = action.centerInside
                        ? Math.max(heightRatio, widthRatio)
                        : Math.min(heightRatio, widthRatio);
            }
        }
        options.inSampleSize = sampleSize;
        options.inJustDecodeBounds = false;
    }
}
