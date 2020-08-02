package com.gochiusa.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 *  基于DiskLruCache封装的磁盘缓存类
 */
public class DiskCache implements Cache {

    private static final int MAX_SIZE = 1024 * 1024 * 3;

    private DiskLruCache mDiskLruCache;

    private Context context;
    public DiskCache(Context context) throws IOException {
        this.context = context;
        mDiskLruCache = open();
    }
    @Override
    public Bitmap get(String key) {
        try {
            // 获取缓存快照
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(
                    Utils.createFileKey(key));
            if (snapshot != null) {
                // 获取输入流，并解析成位图返回
                return BitmapFactory.decodeStream(snapshot.getInputStream(0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 否则返回null
        return null;
    }

    @Override
    public void set(String key, Bitmap bitmap) {
        String fileKey = Utils.createFileKey(key);
        try {
            DiskLruCache.Editor editor = mDiskLruCache.edit(fileKey);
            // 打开输出流，并将位图转换成字节数组，写入
            editor.newOutputStream(0).write(getBitmapBytes(bitmap));
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  打开DiskLruCache
     */
    private DiskLruCache open() throws IOException {
        return DiskLruCache.open(context.getCacheDir(), 1, 1, MAX_SIZE);
    }

    /**
     * 获取位图的字节数组
     */
    private byte[] getBitmapBytes(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // 使用PNG格式压缩图片（实际没有压缩）并写到输出流
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    /**
     *  DiskLruCache自动管理了缓存淘汰
     */
    @Override
    public int size() {
        return 0;
    }

    @Override
    public int maxSize() {
        return MAX_SIZE;
    }

    /**
     *  清除磁盘缓存信息并关闭磁盘缓存
     */
    @Override
    public void clear() {
        try {
            mDiskLruCache.delete();
            mDiskLruCache.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  不实现这个方法
     */
    @Override
    public void setOnRemoveListener(MemoryCache.OnRemoveListener listener) {}
}
