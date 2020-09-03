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
        PreviewImageAdapter.PreviewImageViewHolder> implements Observer {
    private Context mContext;

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

    static final class PreviewImageViewHolder extends RecyclerView.ViewHolder {
        private ItemImageView itemImageView;
        public PreviewImageViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImageView = itemView.findViewById(R.id.iv_ui_preview_image);
        }
    }

    public static final class ItemDragCallback extends ItemTouchHelper.Callback {

        private final RecyclerView.Adapter<?> mAdapter;
        private final SelectedItemCollection mSelectedItemCollection;

        public ItemDragCallback(RecyclerView.Adapter<?> adapter) {
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
