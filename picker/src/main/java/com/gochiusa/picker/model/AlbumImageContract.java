package com.gochiusa.picker.model;

import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

public final class AlbumImageContract {
    /**
     *  总的查询URI
     */
    static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    /**
     * 查询的列
     */
    static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.SIZE};
    /**
     *  WHERE子句，查询全部相册时调用
     */
    static final String SELECTION_ALL_ALBUM =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";
    /**
     * 与查询全部相册时的WHERE子句配套的SQL参数
     */
    static String[] SELECTION_ALL_ALBUM_ARGS = {
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
    };

    /**
     *  WHERE子句，查询特定的一个相册时调用
     */
    static final String SELECTION_ORIGIN_ALBUM =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND "
                    + " bucket_id=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    /**
     *  生成查询特定相册时的WHERE子句配套的SQL参数
     * @param albumId 该相册的ID
     */
    public static String[] getOriginSelectionAlbumArg(String albumId) {
        return new String[] {
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                albumId
        };
    }

    /**
     *  排序子句
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    static final String ORDER_BY = MediaStore.Images.Media.DATE_TAKEN + " DESC";
}
