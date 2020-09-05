package com.gochiusa.picker.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.gochiusa.picker.R;
import com.gochiusa.picker.adapter.PreviewImageAdapter;
import com.gochiusa.picker.adapter.PreviewPageAdapter;
import com.gochiusa.picker.entity.Image;
import com.gochiusa.picker.entity.ImageRequest;
import com.gochiusa.picker.model.SelectedItemCollection;
import com.gochiusa.picker.ui.widget.CheckView;
import com.gochiusa.picker.ui.widget.PreviewDecoration;
import com.gochiusa.picker.util.FragmentManageUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Observer;

import static com.gochiusa.picker.adapter.ImageAdapter.FORMAT_OUT_OF_MAX_TIP;

public class PreviewFragment extends Fragment implements ViewPager.OnPageChangeListener,
        View.OnClickListener {

    private ViewPager mViewPager;
    private CheckView mSelectCheckView;
    private PreviewPageAdapter mPageAdapter;
    private List<Image> mImageList;
    private SelectedItemCollection mSelectedItemCollection;

    private RecyclerView mPreviewRecyclerView;
    private PreviewImageAdapter mPreviewImageAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private ImageWallFragment mImageWallFragment;
    /**
     *  当前展示的图片的下标索引
     */
    private int mNowShowPosition = -1;
    /**
     *  打开这个界面需要显示的第一张图片的位置
     */
    private int mFirstShowPosition;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mSelectedItemCollection = SelectedItemCollection.getInstance();
        // 初始化适配器
        mPageAdapter = new PreviewPageAdapter(mImageList, context);
        mPreviewImageAdapter = new PreviewImageAdapter(
                mSelectedItemCollection.getReadOnlyImageList(), context);
        // 初始化布局管理器
        mLinearLayoutManager = new LinearLayoutManager(context,
                RecyclerView.HORIZONTAL, false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ui_preview, container, false);
        initChildView(view);
        return view;
    }

    private void initChildView(View parent) {
        mViewPager = parent.findViewById(R.id.vp_fragment_preview);
        mSelectCheckView = parent.findViewById(R.id.check_view_preview_select);
        mPreviewRecyclerView = parent.findViewById(R.id.rv_ui_preview);

        initRecyclerView();

        // 为ViewPager添加适配器
        mViewPager.setAdapter(mPageAdapter);
        // 添加ViewPager切换页面时的监听器
        mViewPager.addOnPageChangeListener(this);
        // 设置CheckView的点击事件
        mSelectCheckView.setOnClickListener(this);
        // 调整ViewPager显示的图片的位置
        mViewPager.setCurrentItem(mFirstShowPosition);
        // 修复打开的第一个页面索引为0时的一个bug，刷新底部CheckView的状态
        if (mFirstShowPosition == 0) {
            onPageSelected(mFirstShowPosition);
        }
    }

    /**
     *  设置将会在这个碎片展示的所有图片
     * @param imageData 滑动显示的所有图片
     * @param position 当前应当显示的位置
     */
    public void setImageData(Image[] imageData, int position) {
        mImageList = Arrays.asList(imageData);
        mFirstShowPosition = position;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        // 更新当前显示的图片索引
        mNowShowPosition = position;
        // 查询已选的集合内是否有当前位置的图片的数据
        int nowImageIndex = mSelectedItemCollection.itemIndexOf(mImageList.get(position));
        // 如果有，那么设置CheckView为已选状态，否则为未选状态
        mSelectCheckView.setChecked(nowImageIndex > -1);
        // 将当前的索引通知recyclerView的适配器
        mPreviewImageAdapter.setNowImageIndexAndRefresh(nowImageIndex);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    @Override
    public void onClick(View v) {
        Image nowPositionImage = mImageList.get(mNowShowPosition);

        if (mSelectCheckView.isChecked()) {
            // 如果已经选中，则取消选择
            mSelectCheckView.setChecked(false);
            // 更新下方的RecyclerView
            mPreviewImageAdapter.setNowImageIndexAndRefresh(-1);
            // 从集合中移除图片
            mSelectedItemCollection.removeImage(nowPositionImage);
        } else {
            // 检查是否已经达到了最大可选数
            int maxCount = ImageRequest.getInstance().maxSelectable;
            if (mSelectedItemCollection.getSize() == maxCount) {
                // 超出最大数目，弹出提示，并结束方法
                Toast.makeText(getContext(),
                        String.format(Locale.CHINA, FORMAT_OUT_OF_MAX_TIP, maxCount),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            mSelectCheckView.setChecked(true);
            // 尝试获取该位置上的观察者
            Observer observer = getObserverByPosition(mNowShowPosition);
            if (observer == null) {
                mSelectedItemCollection.addSelectedImage(nowPositionImage);
            } else {
                mSelectedItemCollection.addSelectedItem(nowPositionImage, observer);
            }
            // 更新下方的RecyclerView
            mPreviewImageAdapter.setNowImageIndexAndRefresh(
                    mSelectedItemCollection.itemIndexOf(nowPositionImage));
        }
    }

    @Nullable
    private Observer getObserverByPosition(int position) {
        // 尝试获取显示照片墙的碎片
        if (mImageWallFragment == null) {
            initParentFragment();
        }
        return mImageWallFragment.getWallItemAt(position);
    }

    private void initRecyclerView() {
        // 设置布局管理器
        mPreviewRecyclerView.setLayoutManager(mLinearLayoutManager);
        // 添加自定义分割线
        mPreviewRecyclerView.addItemDecoration(new PreviewDecoration());
        // 设置子项的点击事件
        mPreviewImageAdapter.setGlobalClickListener((image) -> {
            // 计算ViewPager应该跳转的位置
            int currentIndex = mImageList.indexOf(image);
            if (currentIndex > -1) {
                mViewPager.setCurrentItem(currentIndex, true);
            }
        });
        // 添加适配器
        mPreviewRecyclerView.setAdapter(mPreviewImageAdapter);
        mSelectedItemCollection.addObserver(mPreviewImageAdapter);
        new ItemTouchHelper(new PreviewImageAdapter.ItemDragCallback(mPreviewImageAdapter))
                .attachToRecyclerView(mPreviewRecyclerView);
    }

    private void initParentFragment() {
        mImageWallFragment = (ImageWallFragment) FragmentManageUtil.getFragmentManager()
                .findFragmentByTag(ImageWallFragment.WALL_TAG);
    }
}
