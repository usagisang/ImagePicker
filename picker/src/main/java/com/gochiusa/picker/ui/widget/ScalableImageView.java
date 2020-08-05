package com.gochiusa.picker.ui.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatImageView;


public class ScalableImageView extends AppCompatImageView {

    /**
     *  可缩放的标志变量
     */
    private boolean mScalable = false;
    /**
     *  可拖拽的标志变量
     */
    private boolean mCanDrag = false;

    /**
     *  允许的最大缩放倍数
     */
    private static final float MAX_SCALE = 3f;

    /**
     *  允许的最小缩放倍数，由于图片不同，比例也有差异，故进行动态计算
     *  选取能够恰好完全显示在控件内的比例作为最小比例
     */
    private static float sMinScale;
    /**
     *  上一次触控事件的的所有点的中点
     */
    private PointF mLastMidPoint = new PointF();

    /**
     *  上一次的两点距离
     */
    private float mLastDistance;

    /**
     * 进行各种变换的矩阵
     */
    private Matrix mMatrix = new Matrix();

    /**
     *  用于获取坐标信息的矩形
     */
    private RectF mImageRect = new RectF();

    /**
     *  当前控件的几何中心
     */
    private PointF mImageMidden = new PointF();
    /**
     *  当前的缩放倍数
     */
    private float mNowScale = 1f;

    public ScalableImageView(Context context) {
        super(context);
        setScaleType(ScaleType.MATRIX);
    }
    public ScalableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ScaleType.MATRIX);
    }
    public ScalableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        initImgPositionAndSize();
    }

    /**
     * 初始化图片位置和大小
     */
    private void initImgPositionAndSize() {
        mMatrix.reset();
        // 初始化ImageRect
        refreshImageRect();
        // 设置几何中心
        mImageMidden.set(mImageRect.centerX(), mImageRect.centerY());
        // 计算一个比较合适的缩放比例，对图片进行初始化缩放
        sMinScale = Math.min(getWidth() / mImageRect.width(),
                getHeight() / mImageRect.height());

        mNowScale = sMinScale;
        // 缩放
        mMatrix.postScale(sMinScale, sMinScale, mImageRect.centerX(), mImageRect.centerY());
        // 刷新缩放后的矩形
        refreshImageRect();
        // 移动图片到中心
        mMatrix.postTranslate((getRight() - getLeft()) / 2 - mImageRect.centerX(),
                (getBottom() - getTop()) / 2 - mImageRect.centerY());
        commitMatrix();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN :
            case MotionEvent.ACTION_POINTER_DOWN : {
                // 计算本次触控事件的中点
                calculateMidPointOfFinger(event);
                // 重置变量
                mCanDrag = false;
                mScalable = false;
                if (event.getPointerCount() == 2) {
                    // 计算手指距离
                    mLastDistance = calculateDistance(event);
                    mScalable = true;
                } else if (event.getPointerCount() == 1) {
                    mCanDrag = true;
                }
                break;
            }
            // 在手指产生移动距离时，处理平移、缩放事件
            case MotionEvent.ACTION_MOVE: {
                if (mScalable) {
                    scale(event);
                }
                if (mCanDrag) {
                    translate(event);
                }
                if (mCanDrag || mScalable) {
                    commitMatrix();
                }
                break;
            }
            // 在手指放开或者被中断事件时，检查平移、缩放越界
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                // 检查是否越界
                checkScale();
                checkBorder();
                commitMatrix();
                break;
            }
        }
        return true;
    }

    /**
     *  计算触摸事件中所有触控点的中点，实质是求取所有横纵坐标的平均值
     *  如果仅有一点接触，中点即为该点，如果无接触点，则直接返回（0，0）
     */
    private void calculateMidPointOfFinger(MotionEvent event) {
        // 重置点
        mLastMidPoint.set(0f, 0f);
        int pointCount = event.getPointerCount();
        for (int i = 0; i < pointCount; i ++) {
            mLastMidPoint.x += event.getX(i);
            mLastMidPoint.y += event.getY(i);
        }
        mLastMidPoint.x /= pointCount;
        mLastMidPoint.y /= pointCount;
    }

    /**
     *  在两点触控的情况下调用，计算两点之间的距离，多点的情况只取前两点，一点则返会0
     * @return 计算出来的两点距离
     */
    private float calculateDistance(MotionEvent event) {
        if (event.getPointerCount() < 2) {
            return 0f;
        }
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        // 勾股定理
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     *  平移操作
     */
    private void translate(MotionEvent event) {
        // x轴、y轴的移动距离
        float dx = event.getX() - mLastMidPoint.x;
        float dy = event.getY() - mLastMidPoint.y;
        // 重置中点信息
        mLastMidPoint.set(event.getX(), event.getY());
        mMatrix.postTranslate(dx, dy);
    }

    /**
     *  缩放操作
     */
    private void scale(MotionEvent event) {
        // 再次检查手指数
        if (event.getPointerCount() < 2) {
            return;
        }
        // 计算结束距离
        float nowDistance = calculateDistance(event);
        // 计算缩放倍数
        float scale = nowDistance / mLastDistance;
        // 重置距离
        mLastDistance = nowDistance;
        mMatrix.postScale(scale, scale, mImageMidden.x, mImageMidden.y);
        mNowScale *= scale;
        Log.d("this", mNowScale + " : " + nowDistance);
    }

    /**
     *  检查图片平移是否越界，并尝试将图片移回控件中心
     */
    private void checkBorder() {
        // 更新矩形坐标
        refreshImageRect();
        float dx = 0f;
        float dy = 0f;
        // 当矩形宽大于控件时，与控件之间不能有间距
        if (mImageRect.width() > getWidth()) {
            // left坐标大于0说明与子View左边界有间距
            if (mImageRect.left > 0) {
                dx = -mImageRect.left;
            } else if(mImageRect.right < getWidth()) {
                // right坐标比控件宽度小说明与子View右边界有间距
                dx = getWidth() - mImageRect.right;
            }
        } else {
            // 宽小于控件，可以有间距，但是要移动到中心
            dx = getWidth() / 2 - mImageRect.centerX();
        }
        // 当矩形高大于控件时，与控件之间不能有间距
        if (mImageRect.height() > getHeight()) {
            if (mImageRect.top > 0) {
                // top坐标大于0，说明与上边界有间距
                dy = -mImageRect.top;
            } else if(mImageRect.bottom < getHeight()) {
                // bottom坐标小于高度了，说明和子View下边界有间距
                dy = getHeight() - mImageRect.bottom;
            }
        } else {
            // 高小于控件，可以有间距，但是要移动到中心
            dy = getHeight() / 2 - mImageRect.centerY();
        }
        mMatrix.postTranslate(dx, dy);
    }


    /**
     * 检查图片缩放比例是否超过设置的大小
     */
    private void checkScale() {
        PointF scaleCenter = mImageMidden;
        // 缓存校正比例的变量
        float scale = 1f;
        // 根据情况计算校正比例
        if (mNowScale > MAX_SCALE) {
            scale = MAX_SCALE / mNowScale;
        } else if (mNowScale < sMinScale) {
            scale = sMinScale / mNowScale;
        }
        // 设置缩放
        mMatrix.postScale(scale, scale, scaleCenter.x, scaleCenter.y);
        // 重设当前比例
        mNowScale *= scale;
    }

    /**
     * 更新对应实际大小的图片的矩阵，并将矩阵应用到图片
     * 需要更新图片矩阵从而变换图片，应当调用这个方法
     */
    protected void commitMatrix() {
        refreshImageRect();
        setImageMatrix(mMatrix);
    }

    /**
     *  尝试根据矩阵变换矩形，刷新坐标信息
     */
    private void refreshImageRect() {
        if (getDrawable() != null) {
            mImageRect.set(getDrawable().getBounds());
            mMatrix.mapRect(mImageRect, mImageRect);
        }
    }
}
