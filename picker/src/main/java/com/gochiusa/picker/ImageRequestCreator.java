package com.gochiusa.picker;


import android.app.Activity;
import android.content.Intent;

import androidx.annotation.StyleRes;

import com.gochiusa.picker.entity.ImageRequest;
import com.gochiusa.picker.ui.PickerActivity;

public final class ImageRequestCreator {
    private ImagePicker mImagePicker;
    private ImageRequest mImageRequest;


    ImageRequestCreator(ImagePicker imagePicker) {
        mImagePicker = imagePicker;
        mImageRequest = ImageRequest.getCleanInstance();
    }


    public ImageRequestCreator setCountable(boolean countable) {
        mImageRequest.countable = countable;
        return this;
    }

    public ImageRequestCreator setThemeId(@StyleRes int themeId) {
        mImageRequest.themeId = themeId;
        return this;
    }

    public void forResult(int requestCode) {
        Activity activity = mImagePicker.getActivity();
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, PickerActivity.class);
        if (mImagePicker.getFragment() == null) {
            activity.startActivityForResult(intent, requestCode);
        } else {
            mImagePicker.getFragment().startActivityForResult(intent, requestCode);
        }
    }
}
