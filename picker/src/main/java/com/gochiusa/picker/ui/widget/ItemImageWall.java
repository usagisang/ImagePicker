package com.gochiusa.picker.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.gochiusa.picker.R;
import com.gochiusa.picker.entity.Image;
import com.gochiusa.picker.entity.ImageRequest;
import com.gochiusa.picker.model.SelectedItemCollection;

import java.util.Observable;
import java.util.Observer;

public class ItemImageWall extends SquareFrameLayout implements Observer, View.OnClickListener {

    private ImageView mImageView;
    private CheckView mCheckView;

    private ImageRequest mImageRequest;
    private SelectedItemCollection mSelectedItemCollection;

    private Image mImage;
    private Context mContext;
    /**
     *  缓存图片上一次通知更新时在集合中的索引
     */
    private int mImageIndex = -1;

    private OnWallClickListener mWallClickListener;

    public ItemImageWall(@NonNull Context context) {
        super(context);
    }

    public ItemImageWall(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     *  初始化
     */
    public void init(Context context) {
        mImageView = findViewById(R.id.iv_ui_image_wall);
        mCheckView = findViewById(R.id.check_view);
        mImageRequest = ImageRequest.getInstance();
        mContext = context;
        mSelectedItemCollection = SelectedItemCollection.getInstance();
    }

    /**
     *  为这个子布局绑定一个{@link Image}，并刷新CheckView
     */
    public void bindInfo(Image image) {
        mImage = image;
        // 调用图片引擎加载图片
        mImageRequest.getImageEngine().loadThumbnail(
                mContext, 300,
                ContextCompat.getDrawable(getContext(), R.drawable.ic_white_background),
                mImageView, image.getUri());
        registerObserverAgain();
    }

    @Override
    public void onClick(View view) {
        if (mWallClickListener == null) {
            return;
        }
        if (view instanceof ImageView) {
            mWallClickListener.onImageClick(this, mImage);
        }
        if (view instanceof CheckView) {
            mWallClickListener.onCheckViewClick(this, mImage);
        }
    }

    public void setWallClickListener(OnWallClickListener mWallClickListener) {
        this.mWallClickListener = mWallClickListener;
        // 设置监听器
        mImageView.setOnClickListener(this);
        mCheckView.setOnClickListener(this);
    }

    /**
     *  继承Observer而重写的一个方法
     *@param arg 若为{@link SelectedItemCollection#REFRESH_INDEX}，
     *           尝试刷新{@link CheckView}上的显示状态
     *           若为{@link SelectedItemCollection#DETACH_THIS_OBSERVER}，则取消注册这个观察者
     */
    @Override
    public void update(@Nullable Observable o, @Nullable Object arg) {
        if (arg == SelectedItemCollection.REFRESH_INDEX) {
            updateWithIndex();
        }
        if (arg == SelectedItemCollection.DETACH_THIS_OBSERVER) {
            detachThisObserver();
        }
    }

    /**
     *  从集合中获取图片的所有，然后判断并刷新{@link CheckView}的选中状态
     */
    private void updateWithIndex() {
        // 获取索引
        int index = mSelectedItemCollection.itemIndexOf(mImage);
        // 如果索引和之前缓存的索引一致，结束方法
        if (mImageIndex == index) {
            return;
        } else {
            // 否则更新索引
            mImageIndex = index;
        }
        // 如果索引无效
        if (index == -1) {
            // 清除这个观察者
            detachThisObserver();
        } else {
            // 索引有效，根据是否可数而设置显示效果
            if (mImageRequest.countable) {
                // 更新CheckView上的索引
                mCheckView.setCheckNum(index + 1);
            } else {
                mCheckView.setChecked(true);
            }
        }
    }

    /**
     *  取消这个观察者的注册，并复原{@link CheckView}为初始状态
     */
    private void detachThisObserver() {
        // 清除这个观察者
        mSelectedItemCollection.deleteObserver(this);
        if (mImageRequest.countable) {
            // 取消数字显示
            setCheckNum(CheckView.UNSELECTED);
        } else {
            // 取消单选显示
            setChecked(false);
        }
    }

    /**
     *  检测该View是否从观察者队列中不正常移出
     *  若持有的image仍然存在于集合内，那么重新注册这个观察者
     *  最后刷新控件状态
     */
    public void registerObserverAgain() {
        // 获取索引
        int index = mSelectedItemCollection.itemIndexOf(mImage);
        // 如果索引有效，注册观察者
        if (index != -1) {
            // 尝试添加观察者，因为Observable类已经拦截了相同的观察者，所以不会发生重复注册问题
            mSelectedItemCollection.addObserver(this);
        }
        // 刷新控件状态
        update(null, SelectedItemCollection.REFRESH_INDEX);
    }


    public void setCheckNum(int number) {
        mCheckView.setCheckNum(number);
    }

    public void setChecked(boolean check) {
        mCheckView.setChecked(check);
    }

    public CheckView getCheckView() {
        return mCheckView;
    }

    /**
     *  点击监听接口
     */
    public interface OnWallClickListener {
        /**
         *  当图片被点击
         */
        void onImageClick(ItemImageWall itemImageWall, Image image);

        /**
         *  当{@link CheckView}被点击
         */
        void onCheckViewClick(ItemImageWall itemImageWall, Image image);
    }

}
