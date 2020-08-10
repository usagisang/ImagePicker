package com.gochiusa.picker.engine.impl;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.gochiusa.imageloader.ImageLoader;
import com.gochiusa.picker.engine.ImageEngine;


public class DefaultEngine implements ImageEngine {

    private boolean lastInFirstOut;
    private ImageLoader loader;

    public DefaultEngine(boolean LIFO) {
        lastInFirstOut = LIFO;
    }

    @Override
    public void loadThumbnail(Context context, int resize,
                              @Nullable Drawable placeholder, ImageView imageView, Uri uri) {
        if (loader == null) {
            createLoader(context);
        }
        if (placeholder == null) {
            loader.load(uri).resize(resize, resize).centerCrop().into(imageView);
        } else {
            loader.load(uri).resize(resize, resize).centerCrop()
                    .placeHolder(placeholder).into(imageView);
        }
    }

    @Override
    public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        if (loader == null) {
            createLoader(context);
        }
        loader.load(uri).resize(resizeX, resizeY).skipAllCache().into(imageView);
    }

    @Override
    public void loadImage(Context context, ImageView imageView, Uri uri) {
        if (loader == null) {
            createLoader(context);
        }
        loader.load(uri).skipAllCache().into(imageView);
    }

    private void createLoader(Context context) {
        ImageLoader.Builder builder = new ImageLoader.Builder(context);
        if (lastInFirstOut) {
            builder.setLIFO();
        }
        loader = builder.build();
    }
}
