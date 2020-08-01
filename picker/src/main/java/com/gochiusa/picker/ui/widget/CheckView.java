package com.gochiusa.picker.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.gochiusa.picker.R;

public class CheckView extends View {
    /**
     *  预设的值。指示当前未设置选择的数字
     */
    public static final int UNSELECTED = Integer.MIN_VALUE;

    /**
     *  自定义View的宽度，48dp，需要转换成px
     */
    private static final int VIEW_SIZE = 48;
    /**
     *  边框的线的宽度，为3dp，需要转换为px
     */
    private static final float STROKE_WIDTH = 3.0f;
    /**
     *  Checkbox的圆的半径
     */
    private static final float FRAME_RADIUS = 11.5f;

    /**
     *  CheckBox的背景的半径
     */
    private static final float BG_RADIUS = 11.0f;

    /**
     *  密度比例因子
     */
    private float mDensity;

    /**
     *  绘制边框的画笔
     */
    private Paint mFramePaint;

    /**
     *  是否选中
     */
    private boolean mSelected;
    /**
     *  显示的选择数字
     */
    private int mCheckNum = UNSELECTED;

    /**
     *  背景绘制画笔
     */
    private Paint mBackgroundPaint;

    /**
     *  文字绘制画笔
     */
    private TextPaint mTextPaint;

    public CheckView(Context context) {
        super(context);
        initParam(context);
    }

    public CheckView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initParam(context);
    }

    /**
     *  传入一个数字，在控件的框内绘制显示该数字。
     *  多选时，依次传入数字来显示选择顺序，取消选择请传入UNSELECTED
     * @param number 需要显示在CheckBox内的数字，必须大于0或者是UNSELECTED
     */
    public void setCheckNum(int number) {
        if (number != UNSELECTED && number <= 0) {
            throw new IllegalArgumentException("传入的数字必须为正或者为预设的UNSELECTED");
        }
        // 设置状态，如果是设置了未选中
        mSelected = (number != UNSELECTED);
        // 设置数字
        mCheckNum = number;
        // 刷新界面
        invalidate();
    }

    /**
     *  设置这个view是否为选中状态，并刷新View。
     *  单选应当使用这个API来显示选择、取消选择的视觉效果
     */
    public void setSelected(boolean selected) {
        mSelected = selected;
        invalidate();
    }

    /**
     *  根据上下文，初始化参数
     */
    private void initParam(Context context) {
        // 获取密度比例因子
        mDensity = context.getResources().getDisplayMetrics().density;
        // 初始化边框画笔
        initFramePaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // view的大小设置为固定值
        int size = MeasureSpec.makeMeasureSpec((int) mDensity * VIEW_SIZE,
                MeasureSpec.EXACTLY);
        super.onMeasure(size, size);
    }

    /**
     *  初始化绘制边框的Paint
     */
    private void initFramePaint() {
        // 默认开启抗锯齿
        mFramePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // 设置为白色边框
        mFramePaint.setColor(ResourcesCompat.getColor(getResources(),
                R.color.color_white, getContext().getTheme()));
        // 设置为画线模式
        mFramePaint.setStyle(Paint.Style.STROKE);
        // 设置线的宽度
        mFramePaint.setStrokeWidth(STROKE_WIDTH * mDensity);
        // 设置Transfer mode。
        // 以绘制的内容作为源图像，以 View 中已有的内容作为目标图像，选择一个模式来处理
        // 这个模式会同时显示源与目标，重叠部分则以源覆盖目标
        mFramePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 计算View的中心坐标
        float center = (float) VIEW_SIZE * mDensity / 2;
        // 绘制边框圆形
        canvas.drawCircle(center, center, FRAME_RADIUS * mDensity, mFramePaint);
        if (mSelected) {
            // 绘制背景
            initBackgroundPaint();
            canvas.drawCircle(center, center, BG_RADIUS * mDensity, mBackgroundPaint);
            if (mCheckNum != UNSELECTED) {
                initTextPaint();
                // 转换为文本
                String text = String.valueOf(mCheckNum);
                // 定位Text的位置，计算坐标
                // 横坐标为除去字的宽度后的一半，相当于均分了字体两端的边缘
                int dx = (int) (getWidth() - mTextPaint.measureText(text)) / 2;
                // 纵坐标
                int dy = (int) (getHeight() - mTextPaint.descent() - mTextPaint.ascent()) / 2;
                canvas.drawText(text, dx, dy, mTextPaint);
            }
        }
    }

    private void initBackgroundPaint() {
        // 懒加载
        if (mBackgroundPaint == null) {
            // 默认开启抗锯齿
            mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            // 设置为填充模式
            mBackgroundPaint.setStyle(Paint.Style.FILL);
            // 设置背景颜色为主题颜色
            mBackgroundPaint.setColor(ResourcesCompat.getColor(getResources(),
                    R.color.color_picker_primary, getContext().getTheme()));
        }
    }

    private void initTextPaint() {
        if (mTextPaint == null) {
            mTextPaint = new TextPaint();
            // 开启抗锯齿
            mTextPaint.setAntiAlias(true);
            // 设置字体颜色为白色
            mTextPaint.setColor(Color.WHITE);
            // 设置字体大小
            mTextPaint.setTextSize(12.0f * mDensity);
        }
    }

}
