package com.gochiusa.picker.entity;


import com.gochiusa.picker.R;
import com.gochiusa.picker.engine.ImageEngine;
import com.gochiusa.picker.engine.impl.DefaultEngine;

public final class ImageRequest {

    /**
     *  是否为多选模式
     */
    public boolean countable;
    /**
     *  可以选择的最大数量
     */
    public int maxSelectable;
    /**
     *  框架整体使用的主题的资源id
     */
    public int themeId;
    /**
     *  标记是否为后进先加载，默认为先进先加载
     */
    private boolean LIFO;

    /**
     *  图片加载引擎
     */
    private ImageEngine imageEngine;

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
        LIFO = false;
    }

    public ImageEngine getImageEngine() {
        if (imageEngine == null) {
            imageEngine = new DefaultEngine(LIFO);
        }
        return imageEngine;
    }

    public void setImageEngine(ImageEngine imageEngine) {
        this.imageEngine = imageEngine;
    }

    public void setLIFO(boolean LIFO) {
        this.LIFO = LIFO;
    }

    public boolean isLIFO() {
        return LIFO;
    }

    private static final class InstanceHolder {
        private static final ImageRequest REQUEST = new ImageRequest();
    }
}
