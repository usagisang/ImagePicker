package com.gochiusa.picker.entity;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.provider.MediaStore;

public class Image {
    private long id;
    private String mimeType;
    private Uri uri;
    private long size;

    public Image() {}

    public Image(long id, String mimeType, long size) {
        this.id = id;
        this.mimeType = mimeType;
        this.size = size;
        createUri();
    }

    protected Image(Parcel in) {
        id = in.readLong();
        mimeType = in.readString();
        uri = in.readParcelable(Uri.class.getClassLoader());
        size = in.readLong();
    }

    public long getId() {
        return id;
    }

    public long getSize() {
        return size;
    }

    public String getMimeType() {
        return mimeType;
    }

    public Uri getUri() {
        return uri;
    }

    public void setId(long id) {
        this.id = id;
        createUri();
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void setSize(long size) {
        this.size = size;
    }

    private void createUri() {
        // 根据id，初始化能打开这个Image的Uri
        uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
    }

    public static Image valueOf(Cursor cursor) {
        return new Image(cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)),
                cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)),
                cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)));
    }
}
