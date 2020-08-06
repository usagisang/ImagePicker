package com.gochiusa.imageloader;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

import static android.content.ContentResolver.SCHEME_CONTENT;
import static android.content.ContentResolver.SCHEME_FILE;

/**
 * 可以处理来自内容提供器的图片请求
 */
public class ContentStreamRequestHandler extends RequestHandler {

    private Context context;
    public ContentStreamRequestHandler(Context context) {
        this.context = context;
    }
    @Override
    public boolean canHandleRequest(Action data) {
        String scheme = data.uri.getScheme();
        return SCHEME_CONTENT.equals(scheme) || SCHEME_FILE.equals(scheme);
    }

    @Override
    public Bitmap load(Action data) throws IOException {
        ContentResolver contentResolver = context.getContentResolver();
        final BitmapFactory.Options options = createBitmapOptions(data);
        // 如果需要计算尺寸
        if (requiresInSampleSize(options)) {
            InputStream inputStream = contentResolver.openInputStream(data.uri);
            // 如果无法获取输入流，直接退出
            if (inputStream == null) {
                return null;
            }
            BitmapFactory.decodeStream(inputStream, null, options);
            calculateInSampleSize(data.targetWidth, data.targetHeight, options, data);
            inputStream.close();
        }
        return BitmapFactory.decodeStream(
                contentResolver.openInputStream(data.uri), null, options);
    }
}
