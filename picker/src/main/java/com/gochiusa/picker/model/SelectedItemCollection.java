package com.gochiusa.picker.model;

import android.net.Uri;

import androidx.annotation.NonNull;

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
     *  发出通知时传达给观察者的参数
     */
    public static final Object DETACH_THIS_OBSERVER = new Object();
    public static final Object REFRESH_INDEX = new Object();

    /**
     * 将一个新的Image实体类添加到最后一位
     */
    public void addSelectedImage(Image image) {
        mImageList.add(image);
        // 通知更新
        notifyAllObserver(REFRESH_INDEX);
    }

    public int getSize() {
        return mImageList.size();
    }

    /**
     *  选择了一个图片后，添加实体类信息与对应的观察者，观察选择的变化
     */
    public void addSelectedItem(Image image, @NonNull Observer observer) {
        addObserver(observer);
        addSelectedImage(image);
    }

    public void removeImage(Image image) {
        mImageList.remove(image);
        // 通知观察者，某一个选择项已经被移除
        notifyAllObserver(REFRESH_INDEX);
    }

    /**
     *  从集合中移除图片，并尝试通知一个观察者，数据已经被更新。
     * @param image 需要移除的图片
     * @param observer 需要通知的观察者。
     */
    public void removeImageAndNotify(Image image, @NonNull Observer observer) {
        // 为了避免这个观察者已经注册，导致重复通知的问题，先添加
        addObserver(observer);
        // 删除图片，这会通知所有已注册的观察者
        removeImage(image);
        // 再注销掉这个观察者
        deleteObserver(observer);
    }

    public void removeAllImage() {
        mImageList.clear();
        notifyAllObserver(REFRESH_INDEX);
    }

    /**
     *  尝试让观察者们主动与这个类detach，脱离联系
     */
    public void detachObservers() {
        notifyAllObserver(DETACH_THIS_OBSERVER);
    }


    private void notifyAllObserver(Object arg) {
        setChanged();
        notifyObservers(arg);
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

    public Image[] getImageArray() {
        return mImageList.toArray(new Image[0]);
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
        singleton.deleteObservers();
        singleton.mImageList.clear();
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
