package com.gochiusa.imageloader;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

class Action {

    final ImageLoader imageLoader;
    final WeakReference<ImageView> target;
    final int errorDrawableId;
    final Uri uri;
    final String key;
    final boolean skipMemoryCache;
    final boolean skipAllCache;
    final int targetWidth;
    final int targetHeight;
    final boolean centerInside;
    final boolean centerCrop;

    /**
     *  该Action是否被取消
     */
    boolean cancelled;

    Action(ImageLoader imageLoader, ImageView target, String key, Uri uri,
           ActionCreator actionCreator) {
        this.imageLoader = imageLoader;
        this.target = new WeakReference<>(target);
        this.errorDrawableId = actionCreator.mErrorResId;
        this.key = key;
        this.uri = uri;
        this.skipMemoryCache = actionCreator.mSkipMemoryCache;
        this.skipAllCache = actionCreator.mSkipAllCache;
        this.targetWidth = actionCreator.targetWidth;
        this.targetHeight = actionCreator.targetHeight;
        this.centerCrop = actionCreator.mCenterCrop;
        this.centerInside = actionCreator.mCenterInside;
    }

    void cancel() {
        cancelled = true;
    }

    @Nullable ImageView getTarget() {
        return target.get();
    }

    String getKey() {
        return key;
    }

    boolean isCancelled() {
        return cancelled;
    }

    Uri getUri() {
        return uri;
    }

    ImageLoader getImageLoader() {
        return imageLoader;
    }

    /**
     *  加载成功完成后回调这个方法
     */
    void complete(Bitmap result) {
        ImageView target = this.target.get();
        if (target != null) {
            target.setImageBitmap(result);
        }
    }

    /**
     *  加载失败后回调这个方法
     */
    void error() {
        ImageView target = this.target.get();
        if (target != null) {
            target.setImageResource(errorDrawableId);
        }
    }

    boolean hasSize() {
        return targetWidth != 0 || targetHeight != 0;
    }

}
