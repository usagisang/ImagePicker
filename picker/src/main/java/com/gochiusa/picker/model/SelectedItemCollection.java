package com.gochiusa.picker.model;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.gochiusa.imageloader.ImageLoader;
import com.gochiusa.picker.entity.Image;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public final class SelectedItemCollection extends Observable {

    private List<Image> mImageList;
    private static SelectedItemCollection singleton;

    /**
     *  与选择列表伴生的只读列表
     */
    private List<Image> mReadOnlyImageList;

    /**
     * 需要删除观察者的时候发出的通知
     */
    public static final Object DETACH_THIS_OBSERVER = new Object();
    /**
     *  集合内含的项目新增或删除时发出的通知
     */
    public static final Object REFRESH_INDEX = new Object();

    /**
     *  当集合内的项目互相之间位置移动时发出的通知
     */
    public static final Object SWAP_ITEMS = new Object();

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
     * 选择了一个图片后，添加实体类信息与对应的观察者，观察选择的变化
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
     * 从集合中移除图片，并尝试通知一个观察者，数据已经被更新。
     *
     * @param image    需要移除的图片
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
     *  交换集合中两个选择项目的位置
     * @throws IndexOutOfBoundsException 有任意一个下标越界则抛出异常
     */
    public void swap(int indexOne, int indexTwo) {
        Collections.swap(mImageList, indexOne, indexTwo);
        notifyAllObserver(SWAP_ITEMS);
    }

    /**
     * 尝试让观察者们主动与这个类detach，脱离联系
     */
    public void detachObservers() {
        notifyAllObserver(DETACH_THIS_OBSERVER);
    }


    /**
     * 将当前所有已选项目的Uri，封装在数组内返回
     */
    public Uri[] getUriArray() {
        int size = getSize();
        Uri[] result = new Uri[size];
        for (int i = 0; i < size; i++) {
            result[i] = mImageList.get(i).getUri();
        }
        return result;
    }

    public int itemIndexOf(Image image) {
        return mImageList.indexOf(image);
    }


    /**
     * 清除全局单例
     */
    public static void clearCollection() {
        if (singleton != null) {
            singleton.deleteObservers();
            singleton.mImageList.clear();
            singleton.mReadOnlyImageList = null;
            singleton = null;
        }
    }

    /**
     * 双加锁单例模式
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

    /**
     *   获取一个已经被选择的图片的一个不可变的视图
     */
    public List<Image> getReadOnlyImageList() {
        if (mReadOnlyImageList == null) {
            mReadOnlyImageList = Collections.unmodifiableList(mImageList);
        }
        return mReadOnlyImageList;
    }

    private SelectedItemCollection() {
        mImageList = new ArrayList<>();
    }

    private void notifyAllObserver(Object arg) {
        setChanged();
        notifyObservers(arg);
    }
}
