package com.gochiusa.picker.entity;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Image) {
            Image otherImage = (Image) obj;
            return otherImage.uri.equals(this.uri) && otherImage.id == this.id &&
                    otherImage.mimeType.equals(this.mimeType) && otherImage.size == this.size;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + mimeType.hashCode();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = 31 * result + Long.hashCode(id);
            result = 31 * result + Long.hashCode(size);
        } else {
            result = 31 * result + (int) id;
            result = 31 * result + (int) size;
        }
        return result;
    }
}
