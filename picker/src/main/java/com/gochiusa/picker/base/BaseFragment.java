package com.gochiusa.picker.base;

import android.widget.Toast;

import androidx.fragment.app.Fragment;

public abstract class BaseFragment<P extends BasePresenter> extends Fragment {
    /**
     *  饿汉式加载
     */
    private P mPresenter = onBindPresenter();

    /**
     * 交由子类实现如何获得Presenter的方法
     *
     * @return 对应的Presenter
     */
    protected abstract P onBindPresenter();


    public P getPresenter() {
        return mPresenter;
    }

    public void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
