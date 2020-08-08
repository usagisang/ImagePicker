package com.gochiusa.picker.adapter;


public interface ItemClickListener<T> {
    interface GlobalClickListener<V> {
        void onClick(V object);
    }

    void setGlobalClickListener(GlobalClickListener<T> listener);
}
