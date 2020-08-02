package com.gochiusa.http;

import java.io.IOException;

public interface Call {

    /**
     * 返回原始的Request
     */
    Request request();

    /**
     *  发送同步请求
     */
    Response execute() throws IOException;

    /**
     *  建造Call类的接口
     */
    interface Factory {
        Call newCall(Request request);
    }
}
