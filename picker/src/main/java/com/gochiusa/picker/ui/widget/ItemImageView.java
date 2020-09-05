package com.gochiusa.picker.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.res.ResourcesCompat;

import com.gochiusa.picker.R;

/**
 *  显示在预览界面下的已选择项目的预览缩略图的ImageView
 */
public class ItemImageView extends AppCompatImageView {

    /**
     *  标志变量，标记该图片框是否被选中
     */
    private boolean mSelected;

    /**
     *  用于获取图片轮廓的矩形
     */
    private RectF mImageRect = new RectF();

    /**
     *  矩形绘制画笔
     */
    private Paint mRectDrawPaint;

    /**
     *  边框的线的宽度，为3dp，需要转换为px
     */
    private static final float STROKE_WIDTH = 8f;

    /**
     *  密度比例因子
     */
    private float mDensity;

    /**
     *  手势检测器
     */
    private GestureDetector mGestureDetector;

    public ItemImageView(@NonNull Context context) {
        super(context);
        initParam(context);
    }

    public ItemImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initParam(context);
    }

    public void setSelected(boolean isSelected) {
        mSelected = isSelected;
        invalidate();
    }

    public boolean isSelected() {
        return mSelected;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mSelected) {
            refreshImageRect();
            canvas.drawRect(mImageRect, mRectDrawPaint);
        }
    }

    /**
     *  根据上下文，初始化参数
     */
    private void initParam(Context context) {
        // 获取密度比例因子
        mDensity = context.getResources().getDisplayMetrics().density;
        // 初始化边框画笔
        initRectPaint();
    }


    private void initRectPaint() {
        // 默认开启抗锯齿
        mRectDrawPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // 设置颜色
        mRectDrawPaint.setColor(ResourcesCompat.getColor(getResources(),
                R.color.color_green, getContext().getTheme()));
        // 设置为画线模式
        mRectDrawPaint.setStyle(Paint.Style.STROKE);
        // 设置线的宽度
        mRectDrawPaint.setStrokeWidth(STROKE_WIDTH * mDensity);
        // 设置Transfer mode。
        // 以绘制的内容作为源图像，以 View 中已有的内容作为目标图像，选择一个模式来处理
        // 这个模式会同时显示源与目标，重叠部分则以源覆盖目标
        mRectDrawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
    }

    private void initGestureDetector() {
        
    }

    /**
     *  刷新矩阵信息
     */
    private void refreshImageRect() {
        if (getDrawable() != null) {
            mImageRect.set(getDrawable().getBounds());
        }
    }
}
