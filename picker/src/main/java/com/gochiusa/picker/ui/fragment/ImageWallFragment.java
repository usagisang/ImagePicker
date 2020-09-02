package com.gochiusa.picker.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gochiusa.picker.R;
import com.gochiusa.picker.adapter.ImageAdapter;
import com.gochiusa.picker.entity.Image;
import com.gochiusa.picker.model.SelectedItemCollection;
import com.gochiusa.picker.ui.widget.ItemImageWall;
import com.gochiusa.picker.ui.widget.WallDecoration;

import java.util.List;


public class ImageWallFragment extends Fragment {

    /**
     *  添加到碎片管理器时，使用这个来标记此碎片
     */
    public static final String WALL_TAG = "fragment_wall_tag";

    private RecyclerView mRecyclerView;
    private List<Image> mImageList;
    private GridLayoutManager mGridLayoutManager;

    public ImageWallFragment() {
        this(null);
    }

    public ImageWallFragment(List<Image> list) {
        mImageList = list;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_recycler_view, container, false);
        initRecyclerView(view);
        return view;
    }

    private void initRecyclerView(View parent) {
        mRecyclerView = parent.findViewById(R.id.rv_ui_photo_wall);
        mGridLayoutManager = new GridLayoutManager(getContext(), 3);
        // 设置为网格布局
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        // 添加自定义分割线
        mRecyclerView.addItemDecoration(new WallDecoration());
        // 初始化适配器
        mRecyclerView.setAdapter(new ImageAdapter(mImageList));
    }

    @Override
    public void onDestroy() {
        // 碎片销毁之前，请求清除这个碎片注册过的所有观察者
        SelectedItemCollection.getInstance().detachObservers();
        super.onDestroy();
    }

    /**
     *  获取指定位置的照片墙View子项
     */
    @Nullable
    public ItemImageWall getWallItemAt(int position) {
        View view = mGridLayoutManager.findViewByPosition(position);
        if (view instanceof ItemImageWall) {
            return (ItemImageWall) view;
        } else {
            return null;
        }
    }

}
