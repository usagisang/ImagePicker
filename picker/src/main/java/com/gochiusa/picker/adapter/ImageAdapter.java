package com.gochiusa.picker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.gochiusa.picker.R;
import com.gochiusa.picker.entity.Image;
import com.gochiusa.picker.entity.ImageRequest;
import com.gochiusa.picker.model.SelectedItemCollection;
import com.gochiusa.picker.ui.fragment.ImageWallFragment;
import com.gochiusa.picker.ui.fragment.PreviewFragment;
import com.gochiusa.picker.ui.widget.ItemImageWall;
import com.gochiusa.picker.util.FragmentManageUtil;

import java.util.List;
import java.util.Locale;

public class ImageAdapter extends ListAdapter<Image, ImageAdapter.ImageViewHolder>
        implements ItemImageWall.OnWallClickListener {

    public static final String FORMAT_OUT_OF_MAX_TIP = "最多只能选择%d张图片";

    public ImageAdapter(List<Image> list) {
        super(list);
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_image_wall_content, parent, false);
        ImageViewHolder imageViewHolder = new ImageViewHolder(view);
        ItemImageWall itemImageWall = imageViewHolder.itemImageWall;
        // 初始化
        itemImageWall.init(parent.getContext());
        // 设置监听接口
        itemImageWall.setWallClickListener(this);
        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.itemImageWall.bindInfo(getItem(position));
    }

    @Override
    public void onImageClick(ItemImageWall itemImageWall, Image image) {
        // 开启预览图片的Fragment
        PreviewFragment fragment = new PreviewFragment();
        // 设置预览的数据源
        fragment.setImageData(toArray(new Image[0]), getPosition(image));
        // 从碎片管理器中获取正在显示的图片墙碎片
        Fragment nowShowFragment = FragmentManageUtil.getFragmentManager()
                .findFragmentByTag(ImageWallFragment.WALL_TAG);
        // 如果能找到这个碎片，显示的碎片转换为预览图片的碎片
        if (nowShowFragment != null) {
            FragmentManageUtil.addFragmentToBackStack(
                    R.id.fl_ui_container, fragment, nowShowFragment);
        }
    }

    @Override
    public void onCheckViewClick(ItemImageWall itemImageWall, Image image) {
        SelectedItemCollection collection = SelectedItemCollection.getInstance();
        // 获取请求实例
        ImageRequest imageRequest = ImageRequest.getInstance();
        // 如果已经被选中，取消选择
        if (itemImageWall.getCheckView().isChecked()) {
            // 从选择的集合中移除这个Image，并进行通知
            collection.removeImageAndNotify(image, itemImageWall);
        } else {
            // 未被选中，先检查最大可选数目
            // 如果为1，那么就是切换图片操作，先移除所有项目，以免触发提醒
            if (imageRequest.maxSelectable == 1) {
                collection.removeAllImage();
            }
            // 再检查是否已经达到了最大可选数
            if (collection.getSize() == imageRequest.maxSelectable) {
                // 超出最大数目，弹出提示
                Toast.makeText(itemImageWall.getContext(),
                        String.format(Locale.CHINA, FORMAT_OUT_OF_MAX_TIP, imageRequest.maxSelectable),
                        Toast.LENGTH_SHORT).show();
            } else {
                // 未到达最大可选数，则添加进入集合
                collection.addSelectedItem(image, itemImageWall);
            }
        }
    }

    protected static class ImageViewHolder extends RecyclerView.ViewHolder {
        private ItemImageWall itemImageWall;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImageWall = (ItemImageWall) itemView;
        }
    }
}
