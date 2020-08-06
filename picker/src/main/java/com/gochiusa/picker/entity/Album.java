package com.gochiusa.picker.entity;

import android.database.Cursor;
import android.provider.MediaStore;

import com.gochiusa.picker.model.AlbumContract;

public class Album {

    /**
     *  “全部图片”的相册的id
     */
    public static final String ALL_IMAGE_ALBUM_ID = String.valueOf(-2);

    public static final String ALL_IMAGE_ALBUM_NAME = "全部图片";

    private String id;
    private String coverPath;
    private String displayName;
    private int count;

    public Album(String id, String coverPath, String displayName, int count) {
        this.id = id;
        this.coverPath = coverPath;
        this.displayName = displayName;
        this.count = count;
    }
    public int getCount() {
        return count;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getId() {
        return id;
    }

    /**
     *  从特定的结果集中加载出相册实体类
     */
    public static Album valueOf(Cursor cursor) {
        return new Album(
                cursor.getString(cursor.getColumnIndex(AlbumContract.BUCKET_ID)),
                cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)),
                cursor.getString(cursor.getColumnIndex(AlbumContract.BUCKET_DISPLAY_NAME)),
                cursor.getInt(cursor.getColumnIndex(AlbumContract.COLUMN_COUNT)));
    }

    public boolean isEmpty() {
        return count == 0;
    }


    /**
     *  根据id来进行判断，该相册是否为“全部图片”相册
     * @return 如果是“全部图片”相册，则返回true
     */
    public boolean isAllAlbum() {
        return this.id.equals(ALL_IMAGE_ALBUM_ID);
    }

}
