package com.gochiusa.imageloader;

import android.graphics.Bitmap;

public interface Cache {
    /**
     *  从缓存中读取位图
     *  */
    Bitmap get(String key);

    /**
     * 将位图放到到缓存中
     * */
    void set(String key, Bitmap bitmap);

    /**
     * 返回当前缓存已经使用的空间的大小
     * */
    int size();

    /**
     * 返回缓存的最大容量
     * */
    int maxSize();

    /**
     * 清除缓存里的所有数据
     * */
    void clear();

    /**
     *  设置当数据按照Lru算法需要淘汰时的监听器
     */
    void setOnRemoveListener(MemoryCache.OnRemoveListener listener);

    interface OnRemoveListener {
        void onRemove(String key, Bitmap bitmap);
    }
}
