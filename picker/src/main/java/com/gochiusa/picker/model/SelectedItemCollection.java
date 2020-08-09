package com.gochiusa.picker.model;

import android.net.Uri;

import com.gochiusa.imageloader.ImageLoader;
import com.gochiusa.picker.entity.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class SelectedItemCollection extends Observable {

    private List<Image> mImageList;
    private static SelectedItemCollection singleton;

    /**
     * 将一个新的Image实体类添加到最后一位
     */
    public void addSelectedImage(Image image) {
        mImageList.add(image);
        // 通知更新
        notifyAllObserver();
    }

    public int getSize() {
        return mImageList.size();
    }

    /**
     *  选择了一个图片后，添加实体类信息与对应的观察者，观察选择的变化
     */
    public void addSelectedItem(Image image, Observer observer) {
        addObserver(observer);
        addSelectedImage(image);
    }

    public void removeImage(Image image) {
        mImageList.remove(image);
        // 通知观察者，某一个选择项已经被移除
        notifyAllObserver();
    }

    public void removeAllImage() {
        mImageList.clear();
        notifyAllObserver();
    }


    public void notifyAllObserver() {
        setChanged();
        notifyObservers();
    }

    /**
     *  将当前所有已选项目的Uri，封装在数组内返回
     */
    public Uri[] getUriArray() {
        int size = getSize();
        Uri[] result = new Uri[size];
        for (int i = 0; i < size; i ++) {
            result[i] = mImageList.get(i).getUri();
        }
        return result;
    }

    public int itemIndexOf(Image image) {
        return mImageList.indexOf(image);
    }


    private SelectedItemCollection() {
        mImageList = new ArrayList<>();
    }

    /**
     *  清除全局单例
     */
    public static void clearCollection() {
        singleton = null;
    }

    /**
     *  双加锁单例模式
     */
    public static SelectedItemCollection getInstance() {
        if (singleton == null) {
            synchronized (ImageLoader.class) {
                if (singleton == null) {
                    singleton = new SelectedItemCollection();
                }
            }
        }
        return singleton;
    }
}
