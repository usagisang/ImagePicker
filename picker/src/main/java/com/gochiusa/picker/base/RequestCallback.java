package com.gochiusa.picker.base;

/**
 *  异步提交耗时任务时，使用的回调接口，需要在加载结束后回到主线程被回调。
 * @param <T> 泛型，当任务执行成功，期望的返回值类型
 * @param <V> 泛型，当任务未完成时，期望的返回值类型
 */
public interface RequestCallback<T, V> {
    void onResponse(T response);
    void onFailure(V failure);
}
