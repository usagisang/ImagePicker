package com.gochiusa.picker.model;

import android.content.Context;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.core.content.ContentResolverCompat;
import androidx.core.os.CancellationSignal;

import com.gochiusa.picker.base.BaseModel;
import com.gochiusa.picker.entity.Album;
import com.gochiusa.picker.entity.Image;

import java.util.ArrayList;
import java.util.List;

import static com.gochiusa.picker.model.AlbumImageContract.*;

public class ImageLoader implements BaseModel {

    /**
     *  加载一个相册内的所有照片的信息
     * @param context 上下文
     * @param album 相册实体类
     * @return 所有照片的{@code Item}信息
     */
    public List<Image> loadAlbumImage(@NonNull Context context, Album album) {
        String selection;
        String[] selectionArgs;
        String sortOrder;
        // 检查相册内是否缓存了它所包含的图片，如果有缓存，直接使用
        if (album.getImageOfAlbum() != null) {
            return album.getImageOfAlbum();
        }
        // 获取WHERE子句以及配套的参数
        if (album.isAllAlbum()) {
            selection = SELECTION_ALL_ALBUM;
            selectionArgs = SELECTION_ALL_ALBUM_ARGS;
        } else {
            selection = SELECTION_ORIGIN_ALBUM;
            selectionArgs = getOriginSelectionAlbumArg(album.getId());
        }
        // 根据版本不同，确定排序子句
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            sortOrder = ORDER_BY;
        } else {
            sortOrder = null;
        }
        // 查询结果
        Cursor cursor = ContentResolverCompat.query(context.getContentResolver(), QUERY_URI,
                PROJECTION, selection, selectionArgs, sortOrder, new CancellationSignal());
        List<Image> list = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(Image.valueOf(cursor));
            }
        }
        // 将查询结果缓存到相册中
        album.setImageOfAlbum(list);
        return list;
    }
}
