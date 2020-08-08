package com.gochiusa.imageloader;

import android.graphics.Bitmap;

import java.io.IOException;
import java.io.InputStream;

/**
 *  接口设计基本模仿自Picasso
 */
public interface Downloader {

    /**
     * 从internet下载指定的图像{@code url}
     * @param imageUrl 远程图像URL
     * @param skipCache 是否跳过缓存，传入{@code true}以跳过
     * @return {@link Response} 包含了 {@link Bitmap} 响应或者一个{@link InputStream}
     * 的输入流来表示图像数据 ，返回{@code null}以指示加载位图时出现问题
     * @throws IOException 如果请求的URL无法成功加载，将引发IOException
     */
    Response load(String imageUrl, boolean skipCache) throws IOException;

    /**
     *  清除、关闭磁盘缓存
     */
    void shutdown();

    /** 响应流或位图和信息 */
    class Response {
        final InputStream stream;
        final Bitmap bitmap;
        final boolean cached;

        /**
         * 响应图像和信息
         *
         * @param bitmap 图像
         * @param loadedFromCache {@code true}如果加载源来自本地磁盘缓存
         */
        public Response(Bitmap bitmap, boolean loadedFromCache) {
            if (bitmap == null) {
                throw new IllegalArgumentException("Bitmap may not be null.");
            }
            this.stream = null;
            this.bitmap = bitmap;
            this.cached = loadedFromCache;
        }

        /**
         * 响应流和信息
         *
         * @param stream 图片信息流
         * @param loadedFromCache {@code true}如果加载源来自本地磁盘缓存。
         */
        public Response(InputStream stream, boolean loadedFromCache) {
            if (stream == null) {
                throw new IllegalArgumentException("Stream may not be null.");
            }
            this.stream = stream;
            this.bitmap = null;
            this.cached = loadedFromCache;
        }

        /**
         * 包含图像数据的输入流
         * 如果返回{@code null}，那么图像数据将通过{@code getBitmap()}获得
         */
        public InputStream getInputStream() {
            return stream;
        }

        /**
         * 表示图像的位图
         * 如果返回{@code null}，那么图像数据将通过{@code getInputStream()}获得。
         */
        public Bitmap getBitmap() {
            return bitmap;
        }
    }
}
