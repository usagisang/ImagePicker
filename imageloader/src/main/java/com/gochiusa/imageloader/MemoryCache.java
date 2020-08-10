package com.gochiusa.imageloader;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.util.LinkedHashMap;
import java.util.Map;

public class MemoryCache implements Cache {

    private final int maxSize;

    private int size = 0;

    private final LinkedHashMap<String, Bitmap> map;

    private OnRemoveListener listener;

    public MemoryCache(Context context) {
        maxSize = Utils.calculateMemoryCacheSize(context);
        this.map = new LinkedHashMap<>(16, 0.75f, true);
    }

    @Override
    public Bitmap get(@NonNull String key) {
        return map.get(key);
    }

    @Override
    public void set(@NonNull String key, @NonNull Bitmap bitmap) {
        // 加锁操作
        synchronized (this) {
            // 先获取图片大小，并递增到size中
            size += Utils.calculateBitmapSize(bitmap);
            Bitmap previousBitmap = map.put(key, bitmap);
            if (previousBitmap != null) {
                // 如果这个键之前绑定过位图，size递减它的大小
                size -= Utils.calculateBitmapSize(previousBitmap);
            }
        }
        // 然后检查目前储存的位图是否超过了最大容量
        trimToSize(maxSize);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int maxSize() {
        return maxSize;
    }

    @Override
    public void clear() {
        trimToSize(-1);
    }

    /**
     *  从{@code LinkedHashMap}中移除最久未被使用的数据，
     *  直到当前所有数据的总大小，小于或等于{@code requireSize}
     *  这个方法可以检查当前位图总大小是否超出规定的maxSize
     */
    private void trimToSize(int requireSize) {
        while (true) {
            synchronized (this) {
                if (size < requireSize || map.isEmpty()) {
                    // 如果映射被清空或者size达到目标大小，则终止循环
                    break;
                }
                // 迭代map中的处于首位的数据
                Map.Entry<String, Bitmap> entry = map.entrySet().iterator().next();
                String key = entry.getKey();
                Bitmap value = entry.getValue();
                map.remove(key);
                // 减小size
                size -= Utils.calculateBitmapSize(value);
                // 如果Listener不为空，并且是自然的淘汰操作，非清空操作，则调用接口
                if (listener != null && requireSize == maxSize) {
                    listener.onRemove(key, value);
                }
            }
        }
    }

    @Override
    public void setOnRemoveListener(OnRemoveListener listener) {
        this.listener = listener;
    }

    public interface OnRemoveListener {
        void onRemove(String key, Bitmap bitmap);
    }
}
