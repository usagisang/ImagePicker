package com.gochiusa.picker.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
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

    private static final float FLING_DAMPING_FACTOR = 0.9f;

    /**
     *  允许的最大缩放倍数
     */
    private static final float MAX_SCALE = 3f;

    /**
     *  允许的最小缩放倍数，由于图片不同，比例也有差异，故进行动态计算
     *  选取能够恰好完全显示在控件内的比例作为最小比例
     */
    private float mMinScale;
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

    /**
     *  手势检测器
     */
    private GestureDetector mGestureDetector;

    private FlingAnimator mFlingAnimator;

    public ScalableImageView(Context context) {
        super(context);
        setScaleType(ScaleType.MATRIX);
        initGestureDetector();
    }
    public ScalableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ScaleType.MATRIX);
        initGestureDetector();
    }
    public ScalableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setScaleType(ScaleType.MATRIX);
        initGestureDetector();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initImagePositionAndSize();
    }


    /**
     *  重写该方法，以解决滑动冲突，当图片尚未到达横向边界，说明仍可拖拽
     */
    @Override
    public boolean canScrollHorizontally(int direction) {
        refreshImageRect();
        if (direction > 0) {
            return mImageRect.right > getWidth();
        } else {
            return mImageRect.left < 0;
        }
    }

    /**
     * 初始化图片位置和大小
     */
    private void initImagePositionAndSize() {
        mMatrix.reset();
        // 初始化ImageRect
        refreshImageRect();
        // 判断能否进行计算，如果满足下列条件，是无法计算缩放的结果的
        boolean notCalculate = (getWidth() == 0) || (getHeight() == 0) ||
                (mImageRect.height() == 0) || (mImageRect.width() == 0);
        if (notCalculate) {
            return;
        }
        // 设置几何中心
        mImageMidden.set(mImageRect.centerX(), mImageRect.centerY());
        // 计算一个比较合适的缩放比例，对图片进行初始化缩放
        mMinScale = Math.min(getWidth() / mImageRect.width(),
                getHeight() / mImageRect.height());
        mNowScale = mMinScale;
        // 缩放
        mMatrix.postScale(mMinScale, mMinScale, mImageRect.centerX(), mImageRect.centerY());
        // 刷新缩放后的矩形
        refreshImageRect();
        // 移动图片到中心
        mMatrix.postTranslate((getRight() - getLeft()) / 2 - mImageRect.centerX(),
                (getBottom() - getTop()) / 2 - mImageRect.centerY());
        commitMatrix();
    }

    /**
     *  初始化手势检测器和滑动屏幕的辅助类
     */
    private void initGestureDetector() {
        GestureDetector.SimpleOnGestureListener listener =
                new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2,
                                   float velocityX, float velocityY) {
                // 只允许单手指拖动
                if (mCanDrag) {
                    // 取消正在进行的所有动画
                    cancelAllAnimator();
                    // 创建新的滑动动画
                    mFlingAnimator = new FlingAnimator(velocityX / 60,
                            velocityY / 60);
                    mFlingAnimator.start();
                }
                // 刷新界面
                invalidate();
                return true;
            }
        };
        mGestureDetector = new GestureDetector(getContext(), listener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN :
                // 单指按下，则取消所有动画
                cancelAllAnimator();
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
        // 交给手势控制器处理事件
        mGestureDetector.onTouchEvent(event);
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
     *  将图片在X轴与Y轴下滑动若干距离，使用Matrix变换实现
     * @param dx 需要在X轴下滑行的距离
     * @param dy 需要在Y轴下滑行的距离
     * @return 是否在X轴或者Y轴方向成功滑动图片
     */
    private boolean scrollViewByDistance(float dx, float dy) {
        refreshImageRect();
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        // 接下来开始检查一些边界条件，以确定是否干涉滑动距离dx与dy

        // 如果当前图片宽度小于控件宽度，或超过了左、右边界，X轴上不允许滑动，重置X轴滑动距离
        if (mImageRect.width() < viewWidth || mImageRect.left >= 0 ||
                mImageRect.right < viewWidth) {
            dx = 0;
        // 如果图片左边在滑动后超出左边界
        } else if (mImageRect.left + dx > 0) {
            // X轴滑动距离重置为恰好到达左边界的距离
            dx = - mImageRect.left;

        // 如果图片右边在滑动后超出右边界
        } else if (mImageRect.right + dx < viewWidth) {
            // X轴滑动距离重置为恰好到达右边界的距离
            dx = viewWidth - mImageRect.right;

        }
        // 如果当前图片高度小于控件高度，或超过了上、下边界，Y轴上不允许滑动，重置X轴滑动距离
        if (mImageRect.height() < viewHeight || mImageRect.top > 0 ||
                mImageRect.bottom < viewHeight) {
            dy = 0;
        // 如果图片上边缘在滑动后超出上边界
        } else if (mImageRect.top + dy > 0) {
            // Y轴滑动距离重置为恰好到达上边界的距离
            dy = - mImageRect.top;
        // 如果图片下边缘在滑动后超出下边界
        } else if (mImageRect.bottom + dy < viewHeight) {
            // Y轴滑动距离重置为恰好到达下边界的距离
            dy = viewHeight - mImageRect.bottom;
        }
        // 提交滑动
        mMatrix.postTranslate(dx, dy);
        commitMatrix();
        // 检查是否发生滑动
        return dx != 0 || dy != 0;
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
        mMatrix.postScale(scale, scale, mLastMidPoint.x, mLastMidPoint.y);
        mNowScale *= scale;
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
        PointF scaleCenter = mLastMidPoint;
        // 缓存校正比例的变量
        float scale = 1f;
        // 根据情况计算校正比例
        if (mNowScale > MAX_SCALE) {
            scale = MAX_SCALE / mNowScale;
        } else if (mNowScale < mMinScale) {
            scale = mMinScale / mNowScale;
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

    /**
     *  尝试取消正在进行的所有动画
     */
    private void cancelAllAnimator() {
        if (mFlingAnimator != null) {
            mFlingAnimator.cancel();
            mFlingAnimator = null;
        }
    }

    /**
     * 惯性动画
     *
     * 速度逐渐衰减,每帧速度衰减为原来的FLING_DAMPING_FACTOR,当速度衰减到小于1时停止.
     * 当图片不能移动时,动画停止.
     */
    private class FlingAnimator extends ValueAnimator
            implements ValueAnimator.AnimatorUpdateListener {

        /**
         * 缓存每一帧X轴与Y轴的速度
         */
        private float[] mVelocity;

        /**
         * 初始滑动速度参数单位必须为 像素/帧，否则会导致滑动过快的问题
         * @param velocityX X轴的初始滑动速度
         * @param velocityY Y轴的初始滑动速度
         */
        public FlingAnimator(float velocityX, float velocityY) {
            super();
            // 设置属性值从0到1变化，但其实没有再使用这个属性
            setFloatValues(0, 1f);
            // 持续时间设置得比较大，必须通过手动的方式停止动画
            setDuration(1000000);
            addUpdateListener(this);
            mVelocity = new float[] {velocityX, velocityY};
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float velocityX = mVelocity[0];
            float velocityY = mVelocity[1];
            // 移动图像、返回是否移动的布尔值
            boolean result = scrollViewByDistance(velocityX, velocityY);
            // 将速度按照比例衰减
            mVelocity[0] *= FLING_DAMPING_FACTOR;
            mVelocity[1] *= FLING_DAMPING_FACTOR;
            // 速度过小，或者X轴与Y轴都不能移动，结束动画
            if (! result || Math.sqrt(velocityX * velocityX + velocityY * velocityY) < 1f) {
                animation.cancel();
            }
        }
    }
}
