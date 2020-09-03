package com.gochiusa.picker.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

/**
 *  显示在预览界面下的已选择项目的预览缩略图的ImageView
 */
public class ItemImageView extends AppCompatImageView {

    public ItemImageView(@NonNull Context context) {
        super(context);
    }

    public ItemImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
