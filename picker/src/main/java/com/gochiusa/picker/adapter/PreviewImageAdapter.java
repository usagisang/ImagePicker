package com.gochiusa.picker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.gochiusa.picker.R;
import com.gochiusa.picker.entity.Image;
import com.gochiusa.picker.entity.ImageRequest;
import com.gochiusa.picker.model.SelectedItemCollection;
import com.gochiusa.picker.ui.widget.ItemImageView;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static androidx.recyclerview.widget.ItemTouchHelper.DOWN;
import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;
import static androidx.recyclerview.widget.ItemTouchHelper.RIGHT;
import static androidx.recyclerview.widget.ItemTouchHelper.UP;

public class PreviewImageAdapter extends ListAdapter<Image,
        PreviewImageAdapter.PreviewImageViewHolder> implements Observer, ItemClickListener<Image> {

    private Context mContext;
    private GlobalClickListener<Image> mGlobalListener;

    /**
     *  当前显示了选中样式的子项的位置
     */
    private int mNowSelectedPosition = -1;

    public PreviewImageAdapter(List<Image> list, @NonNull Context context) {
        super(list);
        mContext = context;
    }

    @NonNull
    @Override
    public PreviewImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.item_preview_small_image, parent, false);
        return new PreviewImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewImageViewHolder holder, int position) {
        // 绑定点击事件
        holder.itemImageView.setOnClickListener((view) -> {
            if (mGlobalListener != null) {
                mGlobalListener.onClick(getItem(holder.getAdapterPosition()));
            }
        });
        if (mNowSelectedPosition == position) {
            // 稍微优化一下，以免重复触发绘制
            if (! holder.itemImageView.isSelected()) {
                holder.itemImageView.setSelected(true);
            }
        } else {
            // 稍微优化一下，以免重复触发绘制
            if (holder.itemImageView.isSelected()) {
                holder.itemImageView.setSelected(false);
            }
        }
        // 调用图片加载引擎加载图片
        ImageRequest.getInstance().getImageEngine().loadThumbnail(mContext, 300,
                ContextCompat.getDrawable(mContext, R.drawable.ic_photo_album),
                holder.itemImageView, getItem(position).getUri());
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg == SelectedItemCollection.REFRESH_INDEX) {
            notifyDataSetChanged();
        }
    }

    @Override
    public void setGlobalClickListener(GlobalClickListener<Image> listener) {
        mGlobalListener = listener;
    }

    /**
     *  设置当前在{@code ViewPager}上展示的图片的{@code Image}信息，在对应的已选择集合中的索引
     *  并且刷新{@code RecyclerView}的显示。
     *  如果传入一个有效的索引，那么对应位置上的子项将变成被标记样式
     *  由于同时只能存在一个被选中的子项的缘故，因此上一个标记将被清除
     * @param nowSelectedIndex 若传入-1，代表展示的图片不在已选择集合内
     */
    public void setNowImageIndexAndRefresh(int nowSelectedIndex) {
        int lastPosition = mNowSelectedPosition;
        this.mNowSelectedPosition = nowSelectedIndex;
        // 刷新上一次被标记的项目，取消标记
        notifyItemChanged(lastPosition);
        // 刷新这次新选中的项目，加上标记
        notifyItemChanged(mNowSelectedPosition);
    }

    static final class PreviewImageViewHolder extends RecyclerView.ViewHolder {
        private ItemImageView itemImageView;
        public PreviewImageViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImageView = itemView.findViewById(R.id.iv_ui_preview_image);
        }
    }

    public static final class ItemDragCallback extends ItemTouchHelper.Callback {

        private final PreviewImageAdapter mAdapter;
        private final SelectedItemCollection mSelectedItemCollection;

        public ItemDragCallback(PreviewImageAdapter adapter) {
            mAdapter = adapter;
            mSelectedItemCollection = SelectedItemCollection.getInstance();
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder) {
            int dragFlag = LEFT | RIGHT | UP | DOWN;
            return makeMovementFlags(dragFlag, 0);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView,
                              @NonNull RecyclerView.ViewHolder viewHolder,
                              @NonNull RecyclerView.ViewHolder target) {
            // 获取原来的位置
            int fromPosition = viewHolder.getAdapterPosition();
            // 获取拖动到的位置
            int targetPosition = target.getAdapterPosition();
            // 如果在拖动当前选择的项目
            if (fromPosition == mAdapter.mNowSelectedPosition) {
                mAdapter.mNowSelectedPosition = targetPosition;
            } else if (fromPosition < mAdapter.mNowSelectedPosition &&
                    targetPosition >= mAdapter.mNowSelectedPosition) {
                // 如果变动范围正向包含当前已选中的项目
                // 位置减去1即可
                mAdapter.mNowSelectedPosition -= 1;
            } else if (targetPosition <= mAdapter.mNowSelectedPosition &&
                    fromPosition > mAdapter.mNowSelectedPosition) {
                // 如果变动范围逆向包含当前已选中的项目
                // 位置加1即可
                mAdapter.mNowSelectedPosition += 1;
            }
            // 进行位置交换
            if (fromPosition < targetPosition) {
                for (int i = fromPosition; i < targetPosition; i ++) {
                    mSelectedItemCollection.swap(i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > targetPosition; i--) {
                    mSelectedItemCollection.swap(i, i - 1);
                }
            }
            mAdapter.notifyItemMoved(fromPosition, targetPosition);
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {}
    }
}
