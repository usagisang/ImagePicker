package com.gochiusa.picker.model;

import android.content.Context;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.core.content.ContentResolverCompat;

import static com.gochiusa.picker.model.AlbumContract.*;

import com.gochiusa.picker.base.BaseModel;
import com.gochiusa.picker.entity.Album;

import java.util.ArrayList;
import java.util.List;

public class AlbumLoader implements BaseModel {

    public List<Album> loadAlbumFromLocal(@NonNull Context context) {
        List<Album> result = new ArrayList<>();

        Cursor albumIdCursor = ContentResolverCompat.query(context.getContentResolver(), QUERY_URI,
                ALBUM_DETAIL_PROJECTION,
                DETAIL_SELECTION, DETAIL_SELECTION_ARGS,
                null, null);
        // 缓存符合条件的相册的集合
        List<String> idList = new ArrayList<>();
        // 读取相册的id
        while (albumIdCursor.moveToNext()) {
            idList.add(albumIdCursor.getString(albumIdCursor.getColumnIndex(BUCKET_ID)));
        }
        Cursor cursor;
        // 计数变量，累计所有相册的图片数量
        int totalCount = 0;
        // 遍历id，拿到相册的具体信息
        for (String id : idList) {
            cursor = ContentResolverCompat.query(
                    context.getContentResolver(), QUERY_URI, ALBUM_ID_PROJECTION,
                    ID_SELECTION, getIDSelectionArgs(id),
                    null, null);
            // 将行集移到第一行
            cursor.moveToFirst();
            Album album = Album.valueOf(cursor);
            // 将数目累加到变量上
            totalCount += album.getCount();
            result.add(album);
            cursor.close();
        }
        // 创建创建“全部照片”相册，以第一个相册的封面为封面
        Album allImageAlbum = new Album(Album.ALL_IMAGE_ALBUM_ID, result.get(0).getCoverPath(),
                Album.ALL_IMAGE_ALBUM_NAME, totalCount);
        // 将相册插入到第一位
        result.add(0, allImageAlbum);
        return result;
    }
}
