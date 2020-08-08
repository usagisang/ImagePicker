package com.gochiusa.picker.engine;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

public interface ImageEngine {

    void loadThumbnail(Context context, int resize,
                       Drawable placeholder, ImageView imageView, Uri uri);
    void loadImage(Context context, int resizeX,
                   int resizeY, ImageView imageView, Uri uri);
    void loadImage(Context context, ImageView imageView, Uri uri);
}
