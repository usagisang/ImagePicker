package com.gochiusa.picker.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gochiusa.picker.R;
import com.gochiusa.picker.entity.Album;
import com.gochiusa.picker.entity.Image;
import com.gochiusa.picker.entity.ImageRequest;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class AlbumAdapter extends ListAdapter<Album, AlbumAdapter.AlbumViewHolder>
        implements ItemClickListener<Album> {

    /**
     *  格式字符串，带有图片的计数单位
     */
    private static final String FORMAT_COUNT_STRING = "%d张";

    private Context mContext;

    private GlobalClickListener<Album> mListener;


    public AlbumAdapter(List<Album> list, @NonNull Context context) {
        super(list);
        mContext = context;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.item_album_list, parent, false);
        AlbumViewHolder albumViewHolder = new AlbumViewHolder(view);
        // 设置点击监听器
        view.setOnClickListener((v) -> {
            if (mListener != null) {
                mListener.onClick(getItem(albumViewHolder.getAdapterPosition()));
            }
        });
        return albumViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        Album album = getItem(position);
        // 调用图片加载引擎加载图片
        ImageRequest.getInstance().getImageEngine().loadThumbnail(mContext, 200,
                mContext.getDrawable(R.drawable.ic_photo_album), holder.cover,
                Uri.fromFile(new File(album.getCoverPath())));
        holder.albumText.setText(album.getDisplayName());
        holder.photoCount.setText(String.format(Locale.CHINA,
                FORMAT_COUNT_STRING, album.getCount()));
    }

    @Override
    public void setGlobalClickListener(GlobalClickListener<Album> listener) {
        mListener = listener;
    }

    static final class AlbumViewHolder extends RecyclerView.ViewHolder {
        private ImageView cover;
        private TextView albumText;
        private TextView photoCount;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.iv_ui_album_cover);
            albumText = itemView.findViewById(R.id.tv_ui_album_name);
            photoCount = itemView.findViewById(R.id.tv_ui_count);
        }
    }

}
