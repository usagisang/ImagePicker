package com.gochiusa.picker.base;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

public class BasePresenter<V> {

    /**
     * 线程成功执行任务或失败的Message返回码
     */
    protected static final int REQUEST_SUCCESS = 10086;
    protected static final int REQUEST_ERROR = 10087;

    /**
     *  跟主线程绑定的Handler
     */
    private Handler mMainHandler;

    /**
     * 保存对View的弱引用
     */
    private WeakReference<V> mViewReference;

    public BasePresenter(V view) {
        attachView(view);
    }


    public void attachView(V view) {
        mViewReference = new WeakReference<>(view);
    }

    public V getView() {
        return mViewReference.get();
    }

    public boolean isViewAttach() {
        return (mViewReference != null) && (mViewReference.get() != null);
    }

    public void removeAttach() {
        if (mViewReference != null) {
            mViewReference.clear();
            mViewReference = null;
        }
    }

    /**
     *  初始化主线程的Handler
     */
    public void initMainHandler(Handler.Callback callback) {
        mMainHandler = new Handler(Looper.getMainLooper(), callback);
    }


    public Handler getMainHandler() {
        if (mMainHandler == null) {
            throw new NullPointerException("使用绑定主线程的Handler前，请调用initMainHandler()来初始化它");
        }
        return mMainHandler;
    }
}
