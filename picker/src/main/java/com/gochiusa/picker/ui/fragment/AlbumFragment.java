package com.gochiusa.picker.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gochiusa.picker.R;
import com.gochiusa.picker.adapter.AlbumAdapter;
import com.gochiusa.picker.adapter.ItemClickListener;
import com.gochiusa.picker.base.BaseFragment;
import com.gochiusa.picker.base.RequestCallback;
import com.gochiusa.picker.entity.Album;
import com.gochiusa.picker.entity.Image;
import com.gochiusa.picker.ui.presenter.AlbumPresenter;
import com.gochiusa.picker.util.FragmentManageUtil;

import java.util.List;

public class AlbumFragment extends BaseFragment<AlbumPresenter> implements
        RequestCallback<List<Album>, String>, ItemClickListener.GlobalClickListener<Album> {

    private AlbumAdapter mAlbumAdapter;
    private RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_recycler_view, container, false);
        initChildView(view);
        getPresenter().requestAlbum();
        return view;
    }

    private void initChildView(View parent) {
        mRecyclerView = parent.findViewById(R.id.rv_ui_photo_wall);
        // 添加分隔线
        mRecyclerView.addItemDecoration(new DividerItemDecoration(
                parent.getContext(), DividerItemDecoration.VERTICAL));
        // 设置RecyclerView为线性布局
        mRecyclerView.setLayoutManager(new LinearLayoutManager(
                parent.getContext()));
    }

    /**
     *  打开显示图片墙的碎片
     */
    public void registerImageFragment(List<Image> list) {
        ImageWallFragment imageWallFragment = new ImageWallFragment(list);
        // 添加新的碎片
        FragmentManageUtil.getFragmentManager().beginTransaction()
                .add(R.id.fl_ui_container, imageWallFragment, ImageWallFragment.WALL_TAG)
                .hide(this).addToBackStack(null).commit();
    }


    @Override
    protected AlbumPresenter onBindPresenter() {
        return new AlbumPresenter(this);
    }

    @Override
    public void onResponse(List<Album> response) {
        if (getContext() != null) {
            mAlbumAdapter = new AlbumAdapter(response, getContext());
            mRecyclerView.setAdapter(mAlbumAdapter);
            // 设置监听器
            mAlbumAdapter.setGlobalClickListener(this);
        }
    }

    @Override
    public void onFailure(String failure) {
        showToast(failure);
    }

    @Override
    public void onClick(Album album) {
        // 向Presenter发起加载请求
        getPresenter().requestAlbumImage(album);
    }
}
