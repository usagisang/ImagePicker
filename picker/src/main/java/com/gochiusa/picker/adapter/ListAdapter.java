package com.gochiusa.picker.adapter;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


/**
 *   封装了对List数据源的基本操作的一个适配器类
 * @param <T> 数据类型
 * @param <V> ViewHolder的类型
 */
public abstract class ListAdapter<T, V extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<V> {

    /**
     *  数据的集合
     */
    private List<T> mList;

    /**
     * 线程锁
     */
    private final Object mLock = new Object();


    public void clear() {
        synchronized (mLock) {
            mList.clear();
            notifyDataSetChanged();
        }
    }

    public void addAll(@NonNull List<T> newList) {
        synchronized (mLock) {
            int originSize = getItemCount();
            if (mList.addAll(newList)) {
                // 如果addAll添加成功，通知刷新
                // （在ArrayList中，其实是做了一个加入的Collection长度是否为0的判断）
                notifyItemRangeInserted(originSize, newList.size());
            }
        }
    }

    public void add(T object) {
        synchronized (mLock) {
            mList.add(object);
            // 通知View，最新的项目插入到列表最后一位
            notifyItemInserted(getItemCount() -1);
        }
    }

    public void remove(T object) {
        synchronized (mLock) {
            // 获取移除的位置
            int position = getPosition(object);
            if (mList.remove(object)) {
                // 如果移除成功，则先通知子项已经被删除
                notifyItemRemoved(position);
                // 然后从被移除的位置开始通知，刷新后续子项的位置position，使之不错位
                notifyItemRangeChanged(position, getListItemCount() - position);
            }
        }
    }

    public T[] toArray(T[] array) {
        return mList.toArray(array);
    }

    public ListAdapter(List<T> list) {
        mList = list;
    }

    public T getItem(int position) {
        return mList.get(position);
    }

    public int getPosition(@NonNull T item) {
        return mList.indexOf(item);
    }

    /**
     * 获取列表里所有元素的个数
     */
    public int getListItemCount() {
        return mList.size();
    }

    @Override
    public int getItemCount() {
        return getListItemCount();
    }
}
