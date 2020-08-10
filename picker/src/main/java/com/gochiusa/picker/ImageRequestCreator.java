package com.gochiusa.picker;


import android.app.Activity;
import android.content.Intent;

import androidx.annotation.StyleRes;

import com.gochiusa.picker.engine.ImageEngine;
import com.gochiusa.picker.entity.ImageRequest;
import com.gochiusa.picker.ui.PickerActivity;

public final class ImageRequestCreator {
    private ImagePicker mImagePicker;
    private ImageRequest mImageRequest;


    ImageRequestCreator(ImagePicker imagePicker, boolean countable) {
        mImagePicker = imagePicker;
        mImageRequest = ImageRequest.getCleanInstance();
        mImageRequest.countable = countable;
    }


    public ImageRequestCreator setCountable(boolean countable) {
        mImageRequest.countable = countable;
        return this;
    }

    public ImageRequestCreator setThemeId(@StyleRes int themeId) {
        mImageRequest.themeId = themeId;
        return this;
    }

    public ImageRequestCreator setLIFO(boolean isLIFO) {
        mImageRequest.setLIFO(isLIFO);
        return this;
    }

    public ImageRequestCreator setImageEngine(ImageEngine imageEngine) {
        mImageRequest.setImageEngine(imageEngine);
        return this;
    }

    public ImageRequestCreator setMaxCount(int maxCount) {
        mImageRequest.maxSelectable = maxCount;
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
