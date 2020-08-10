package com.gochiusa.imageloader;

import android.content.Context;

import com.gochiusa.http.Call;
import com.gochiusa.http.HttpClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 *  使用Http包的网络请求工具类实现的图片加载器
 */
public class HttpDownloader implements Downloader {

    /**
     *  磁盘缓存
     */
    private Cache mDiskCache;

    HttpDownloader(Context context) {
        // 尝试初始化二级缓存
        try {
            mDiskCache = new DiskCache(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Response load(String imageUrl, boolean skipCache) throws IOException {
        if ((!skipCache) && (mDiskCache != null) && (mDiskCache.get(imageUrl) != null)) {
            // 如果允许读取缓存，而且缓存命中，则直接返回Response
            return new Response(mDiskCache.get(imageUrl), true);
        }
        // 直接使用默认方法创建Call
        Call call = HttpClient.createDefaultCall(imageUrl);
        com.gochiusa.http.Response httpResponse = call.execute();
        // 获取响应的字节数组
        byte[] responseBytes = httpResponse.getResponseBody().toByteArray();
        return new Response(new ByteArrayInputStream(responseBytes), false);
    }


    @Override
    public void shutdown() {
        if (mDiskCache != null) {
            mDiskCache.clear();
        }
        mDiskCache = null;
    }

    /**
     *  生成一个监听接口，用于监听内存缓存的位图淘汰，并将淘汰的数据转移到磁盘缓存
     */
    MemoryCache.OnRemoveListener createMemoryCacheRemoveListener() {
        return (key, bitmap) -> {
            if (mDiskCache != null) {
                mDiskCache.set(key, bitmap);
            }
        };
    }
}
