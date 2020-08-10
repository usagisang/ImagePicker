package com.gochiusa.picker.ui.presenter;

import android.os.Handler;
import android.os.Message;

import com.gochiusa.picker.base.BasePresenter;
import com.gochiusa.picker.entity.Album;
import com.gochiusa.picker.entity.Image;
import com.gochiusa.picker.model.AlbumLoader;
import com.gochiusa.picker.model.ImageLoader;
import com.gochiusa.picker.ui.fragment.AlbumFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AlbumPresenter extends BasePresenter<AlbumFragment> {

    private ExecutorService mExecutorService;
    private AlbumLoader mAlbumLoader;
    private ImageLoader mImageLoader;

    private static final String ERROR_TIP = "加载失败";

    /**
     *  存放Runnable对象对应的相册图片加载结果，Runnable是一个会被Handler推至主线程执行的Runnable
     */
    private Map<Runnable, List<Image>> mTaskResultMap;

    public AlbumPresenter(AlbumFragment view) {
        super(view);
        initMainHandler(createCallback());
        // 初始化Model
        mAlbumLoader = new AlbumLoader();
        mImageLoader = new ImageLoader();
        // 初始化线程池
        mExecutorService = Executors.newSingleThreadExecutor();
        mTaskResultMap = new HashMap<>();
    }

    /**
     *  提交加载相册的额请求
     */
    public void requestAlbum() {
        mExecutorService.submit(createAlbumTask());
    }

    /**
     * 提交加载相册的具体数据的请求
     * @param album 需要加载数据的相册实体类
     */
    public void requestAlbumImage(Album album) {
        // 确保View没有detach
        if (isViewAttach()) {
            if (getView().getContext() != null) {
                mExecutorService.submit(() -> {
                    // 加载相册的图片
                    List<Image> list = mImageLoader.loadAlbumImage(getView().getContext(), album);
                    // 创建推到主线程执行的任务
                    Runnable runnable = createLoadedImageRunnable();
                    // 结果与任务建立映射
                    mTaskResultMap.put(runnable, list);
                    getMainHandler().post(runnable);
                });
            } else {
                // 无法获得Context，弹出错误提示
                getView().showToast(ERROR_TIP);
            }
        }
    }

    /**
     *  创建加载相册的可执行任务
     */
    private Runnable createAlbumTask() {
        return () -> {
                Message message = Message.obtain();
                // 失去与View的联系、无法获取上下文将标记为获取失败
                if (isViewAttach() && getView().getContext() != null) {
                    message.obj = mAlbumLoader.loadAlbumFromLocal(getView().getContext());
                    message.what = REQUEST_SUCCESS;
                } else {
                    message.what = REQUEST_ERROR;
                }
                getMainHandler().sendMessage(message);
        };
    }
    /**
     *  创建完成加载相册具体内容的任务后，被推回至主线程执行的{@code Runnable}
     */
    private Runnable createLoadedImageRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                // 获取加载结果
                List<Image> result = mTaskResultMap.get(this);
                // 确保View没有detach
                if (isViewAttach()) {
                    // 让View打开新的碎片
                    getView().registerImageFragment(result);
                }
            }
        };
    }

    public Handler.Callback createCallback() {
        return (message) -> {
            List<Album> result = (List<Album>) message.obj;
            if (isViewAttach()) {
                if (message.what == REQUEST_SUCCESS) {
                    getView().onResponse(result);
                } else {
                    getView().onFailure(ERROR_TIP);
                }
            }
            return true;
        };
    }
}
