package com.gochiusa.imageloader;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

public class ActionCreator {

    private ImageLoader mImageLoader;
     /**
     *  需要显示的图片的Uri地址
     */
    private Uri mUri;

    private String mKey;
    /**
     *  在请求到具体的图片前，是否使用预加载图
     */
    private boolean setPlaceHolder;
    /**
     *  预加载图的图片
     */
    private Drawable mPlaceHolderDrawable;
    /**
     * 预加载图的图片资源id
     */
    private int mPlaceHolderId;

    private boolean mSkipMemoryCache;

    /**
     *  图片的目标宽高
     */
    int targetWidth;
    int targetHeight;

    /**
     *  加载错误后显示的图片的资源id
     */
    private int mErrorResId;

    /**
     *  策略：完全缩放图片直到长宽都能放进ImageView
     */
    boolean mCenterInside;
    /**
     * 策略：先稍微缩放图片以使图片适应长宽其中一个维度，然后另一个过长的维度将被裁剪。
     */
    boolean mCenterCrop;

    ActionCreator(ImageLoader imageLoader, Uri uri, String key) {
        this.mImageLoader = imageLoader;
        this.mUri = uri;
        this.mKey = key;
    }

    public void into(ImageView target) {
        // 如果设置了占位图，则显示
        if (setPlaceHolder) {
            target.setImageDrawable(getPlaceHolder());
        }
        if (! mSkipMemoryCache) {
            // 从内存缓存中尝试检索位图
            Bitmap cacheBitmap = mImageLoader.quickMemoryCacheCheck(mKey);
            if (cacheBitmap != null) {
                // 尝试取消请求
                mImageLoader.cancelRequest(target);
                // 设置位图后结束调用
                target.setImageBitmap(cacheBitmap);
                return;
            }
        }
        // 如果设置了这个两个模式，并且没有合法的目标宽高
        if ((mCenterInside || mCenterCrop) && (! hasSize())) {
            // 尝试获取目标ImageView的宽高
            int width = target.getWidth();
            int height = target.getHeight();
            // 如果仍然无法获取准确的宽高，取消所选模式
            if (width == 0 || height == 0) {
                mCenterCrop = false;
                mCenterInside = false;
            } else {
                resize(width, height);
            }
        }
        Action action = new Action(mImageLoader, mErrorResId, target, mKey, mUri,
                mSkipMemoryCache, this);
        // 提交请求
        mImageLoader.enqueueAndSubmit(action);
    }

    public ActionCreator placeHolder(Drawable drawable) {
        this.mPlaceHolderDrawable = drawable;
        this.setPlaceHolder = true;
        return this;
    }

    public ActionCreator placeHolder(@DrawableRes int resId) {
        this.mPlaceHolderId = resId;
        this.setPlaceHolder = true;
        return this;
    }

    public ActionCreator error(@DrawableRes int resId) {
        this.mErrorResId = resId;
        return this;
    }

    public ActionCreator centerCrop() {
        if (mCenterInside) {
            throw new IllegalStateException("调用centerInside后不能调用centerCrop");
        }
        mCenterCrop = true;
        return this;
    }

    public ActionCreator centerInside() {
        if (mCenterCrop) {
            throw new IllegalStateException("调用centerCrop后不能调用centerInside");
        }
        mCenterInside = true;
        return this;
    }

    /**
     *  忽略缓存，若忽略加载结果则不会进入缓存，也不会尝试从缓存读取数据
     */
    public ActionCreator skipMemoryCache() {
        mSkipMemoryCache = true;
        return this;
    }

    @Nullable
    public Drawable getPlaceHolder() {
        if (setPlaceHolder) {
            if (mPlaceHolderDrawable == null) {
                return mImageLoader.context.getDrawable(mPlaceHolderId);
            } else {
                return mPlaceHolderDrawable;
            }
        }
        return null;
    }

    public ActionCreator resize(int targetWidth, int targetHeight) {
        if (targetWidth < 0) {
            throw new IllegalArgumentException("宽度必须为正数或0");
        }
        if (targetHeight < 0) {
            throw new IllegalArgumentException("高度必须为正数或0");
        }
        if (targetHeight == 0 && targetWidth == 0) {
            throw new IllegalArgumentException("宽度与高度必须至少有一个为正数");
        }
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
        return this;
    }

    private boolean hasSize() {
        return targetWidth != 0 || targetHeight != 0;
    }
}
