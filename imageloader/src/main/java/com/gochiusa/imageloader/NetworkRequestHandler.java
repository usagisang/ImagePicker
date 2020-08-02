package com.gochiusa.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

class NetworkRequestHandler extends RequestHandler {

    private static final String SCHEME_HTTP = "http";
    private static final String SCHEME_HTTPS = "https";

    Downloader mDownloader;

    public NetworkRequestHandler(Downloader downloader) {
        mDownloader = downloader;
    }
    @Override
    public boolean canHandleRequest(Action data) {
        String scheme = data.uri.getScheme();
        return (SCHEME_HTTP.equals(scheme) || SCHEME_HTTPS.equals(scheme));
    }

    @Override
    public Bitmap load(Action data) throws IOException {
        Downloader.Response response = mDownloader.load(data.key);
        Bitmap bitmap = response.getBitmap();
        if (bitmap != null) {
            return bitmap;
        }
        InputStream inputStream = response.getInputStream();
        if (inputStream == null) {
            return null;
        } else {
            return decodeInputStream(inputStream, data);
        }

    }

    private Bitmap decodeInputStream(InputStream inputStream, Action action) throws IOException {
        // 创建Bitmap选项
        final BitmapFactory.Options options = createBitmapOptions(action);
        boolean requireCalculate = requiresInSampleSize(options);
        // 创建字节数组
        byte[] bytes = Utils.toByteArray(inputStream);
        // 如果需要计算图片缩放的比例
        if (requireCalculate) {
            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
            calculateInSampleSize(action.targetWidth, action.targetHeight, options, action);
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }
}
