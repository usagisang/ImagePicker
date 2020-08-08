package com.gochiusa.picker.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.gochiusa.picker.R;
import com.gochiusa.picker.adapter.PreviewPageAdapter;
import com.gochiusa.picker.entity.Image;

import java.util.Arrays;
import java.util.List;

public class PreviewFragment extends Fragment {

    private ViewPager mViewPager;
    private PreviewPageAdapter mPageAdapter;
    private List<Image> mImageList;
    private int mPosition;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // 初始化适配器
        mPageAdapter = new PreviewPageAdapter(mImageList, context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ui_preview, container, false);
        mViewPager = view.findViewById(R.id.vp_fragment_preview);
        // 为ViewPager添加适配器
        mViewPager.setAdapter(mPageAdapter);
        // 调整ViewPager显示的图片的
        mViewPager.setCurrentItem(mPosition);
        return view;
    }

    /**
     *  设置将会在这个碎片展示的所有图片
     * @param imageData 滑动显示的所有图片
     * @param position 当前应当显示的位置
     */
    public void setImageData(Image[] imageData, int position) {
        mImageList = Arrays.asList(imageData);
        mPosition = position;
    }
}
