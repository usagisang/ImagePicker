package com.gochiusa.imageloader;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.content.Context.ACTIVITY_SERVICE;

final class Utils {
    /**
     *  内存缓存允许的最大容量，20M
     */
    private static final int MAX_MEMORY_CACHE_SIZE = 20 * 1024 * 1024;

    /**
     *  计算创建的内存缓存的大小，默认为可用内存的1/8,但不超过MAX_MEMORY_CACHE_SIZE
     */
    static int calculateMemoryCacheSize(Context context) {
        ActivityManager activityManager = (ActivityManager)
                context.getSystemService(ACTIVITY_SERVICE);
        if (activityManager != null) {
            int size = 1024 * 1024 * activityManager.getMemoryClass() / 8;
            return Math.min(size, MAX_MEMORY_CACHE_SIZE);
        } else {
            // 如果没有获取到ActivityManager则默认按照20M处理
            return MAX_MEMORY_CACHE_SIZE;
        }
    }

    /**
     *  获取Bitmap占用空间的大小
     */
    static int calculateBitmapSize(Bitmap bitmap) {
        return bitmap.getAllocationByteCount();
    }

    /**
     *  DiskLruCache只支持有限的字符当作键值，该方法将网址使用MD5计算摘要值
     * @param originKey 未经规范化的键值
     * @return 转换之后的键值
     */
    static String createFileKey(String originKey) {
        return stringToMD5(originKey);
    }

    /**
     *  将字符串以MD5算法进行计算后，生成新的字符串
     *  注意返回的不是严格意义的MD5的字符串值，MD5值小于32位，忽略了在前面补0这一步
     */
    public static String stringToMD5(String plainText) {
        byte[] secretBytes;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            secretBytes = new byte[0];
        }
        return new BigInteger(1, secretBytes).toString(16);
    }

    public static byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int count;
        // 缓存字节数组
        byte[] bytes = new byte[1024 * 4];
        while ((count = inputStream.read(bytes)) > 0) {
            outputStream.write(bytes, 0, count);
        }
        byte[] result = outputStream.toByteArray();
        outputStream.close();
        return result;
    }
}
