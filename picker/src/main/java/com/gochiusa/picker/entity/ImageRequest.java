package com.gochiusa.picker.entity;


import com.gochiusa.picker.R;

public final class ImageRequest {

    public boolean countable;
    public int maxSelectable;
    public int themeId;

    private ImageRequest() {}

    public static ImageRequest getInstance() {
        return InstanceHolder.REQUEST;
    }

    public static ImageRequest getCleanInstance() {
        InstanceHolder.REQUEST.reset();
        return getInstance();
    }

    private void reset() {
        countable = false;
        maxSelectable = 1;
        themeId = R.style.PickerTheme;
    }

    private static final class InstanceHolder {
        private static final ImageRequest REQUEST = new ImageRequest();
    }
}
