package com.gochiusa.picker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.gochiusa.picker.R;
import com.gochiusa.picker.entity.Image;
import com.gochiusa.picker.entity.ImageRequest;
import com.gochiusa.picker.ui.widget.ScalableImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PreviewPageAdapter extends PagerAdapter {
    private List<Image> mImageList;
    private Context mContext;

    public PreviewPageAdapter(@NonNull Image[] images,  @NonNull Context context) {
        mContext = context;
        mImageList = Arrays.asList(images);
    }

    public PreviewPageAdapter(@NonNull List<Image> list, @NonNull Context context) {
        mImageList = new ArrayList<>(list);
        mContext = context;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        // 创建新的View
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_preview_image, null);
        ScalableImageView scalableImageView = view.findViewById(R.id.iv_adapter_preview);
        // 获取图片加载引擎并加载图片
        ImageRequest.getInstance().getImageEngine().loadImage(mContext,
                scalableImageView, mImageList.get(position).getUri());
        // 添加到容器中
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mImageList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        // 使用view进行比较
        return view == object;
    }
}
