package com.gochiusa.picker.model;

import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

public final class AlbumContract {
    public static final String COLUMN_COUNT = "count";
    public static final String BUCKET_ID = "bucket_id";
    public static final String BUCKET_DISPLAY_NAME = "bucket_display_name";
    static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");

    /**
     *  查询满足条件的相册的具体id时，返回哪些列
     */
    static final String[] ALBUM_DETAIL_PROJECTION = new String[] {"DISTINCT " + BUCKET_ID};
    /**
     *  WHERE子句，限制返回的相册类型
     */
    static final String DETAIL_SELECTION =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";
    /**
     * WHERE子句的参数
     */
    static final String[] DETAIL_SELECTION_ARGS = {
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
    };

    /**
     *  查询相册具体信息时，返回哪些列
     */
    static final String[] ALBUM_ID_PROJECTION = {
            BUCKET_ID,
            BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.DATA,
            "COUNT(*) AS " + COLUMN_COUNT};

    /**
     * WHERE子句，指定相册的id
     */
    static final String ID_SELECTION = BUCKET_ID + "=?";

    static String[] getIDSelectionArgs(String id) {
        return new String[]{id};
    }
}
