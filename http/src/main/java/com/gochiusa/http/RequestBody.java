package com.gochiusa.http;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.OutputStream;

/**
 *  类的设计源自OkHttp
 */
public abstract class RequestBody {

    /**
     * 返回请求体的内容类型，如"image/png"，符合MIME的String
     * */
    public abstract @Nullable String contentType();
    /**
     * 返回调用 writeTo 方法时写入 outputSteam的字节数。如果未知，返回-1。
     * 默认返回-1，应当根据需要重写。
     */
    public long contentLength() throws IOException {
        return -1;
    }
    /**
     *  将请求内容写入到{OutputStream outputStream}中临时保存
     */
    public abstract void writeTo(OutputStream outputStream) throws IOException;
    /**
     *  创建一个RequestBody，传入的消息内容是String，一般用于向服务器端提交String
     * @param contentType 传递的内容的类型，可以为null
     * @param content POST请求的内容，使用非UTF-8字符可能会出现异常
     */
    public static RequestBody create(@Nullable String contentType, @NonNull String content) {
        return create(contentType, content.getBytes());
    }


    /**
     *  创建一个RequestBody，传入的消息内容是字节数组，一般用于向服务器端提交数据流
     * @param contentType 传递的内容的类型，可以为null
     * @param bytes POST请求的数据流
     */
    public static RequestBody create(@Nullable final String contentType, @NonNull final byte[] bytes) {
        return new RequestBody() {
            @Nullable
            @Override
            public String contentType() {
                return contentType;
            }

            @Override
            public long contentLength() {
                return bytes.length;
            }

            @Override
            public void writeTo(OutputStream outputStream) throws IOException {
                outputStream.write(bytes);
            }
        };
    }


}
