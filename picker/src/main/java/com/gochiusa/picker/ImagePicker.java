package com.gochiusa.picker;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.lang.ref.WeakReference;

public final class ImagePicker {
    private WeakReference<Activity> mActivity;
    private WeakReference<Fragment> mFragment;

    private ImagePicker(Activity activity, Fragment fragment) {
        mActivity = new WeakReference<>(activity);
        mFragment = new WeakReference<>(fragment);
    }

    /**
     *  使用Activity开启图片选择器
     */
    public static ImagePicker from(Activity activity) {
        return new ImagePicker(activity, null);
    }

    public static ImagePicker from(@NonNull Fragment fragment) {
        return new ImagePicker(fragment.getActivity(), fragment);
    }

    public ImageRequestCreator choose(boolean countable) {
        return new ImageRequestCreator(this, countable);
    }

    @Nullable
    Activity getActivity() {
        return mActivity.get();
    }

    @Nullable
    Fragment getFragment() {
        if (mFragment == null) {
            return null;
        }
        return mFragment.get();
    }
}
