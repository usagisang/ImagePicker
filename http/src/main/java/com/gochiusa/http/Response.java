package com.gochiusa.http;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;

public final class Response {

    /**
     *  响应头的几个参数
     */
    public static final String CONTENT_TYPE = "content-type";
    public static final String CONTENT_ENCODING = "content-encoding";

    /**
     *  与响应对应的请求
     */
    final Request request;
    /**
     *  响应码，如果无法识别响应的HTTP码，则为-1
     */
    final int code;
    /**
     * 响应的信息
     */
    @Nullable
    final String message;
    /**
     * 响应头
     */
    @Nullable
    final Headers responseHeader;

    /**
     *  响应体，使用{@code ByteArrayOutputStream}的形式储存。
     *  可以兼容获取图片响应与获取String字符串响应的需求。
     */
    final ByteArrayOutputStream responseBody;

    private Response(Builder builder) {
        this.code = builder.mCode;
        this.message = builder.mMessage;
        this.request = builder.mRequest;
        this.responseBody = builder.mResponseBody;
        this.responseHeader = builder.mHeader;
    }

    @Nullable
    public Headers getResponseHeader() {
        return responseHeader;
    }

    public Request getRequest() {
        return request;
    }

    public int getCode() {
        return code;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    public String getResponseBodyString() {
        return responseBody.toString();
    }

    public ByteArrayOutputStream getResponseBody() {
        return responseBody;
    }

    /**
     * 建造者模式
     */
    public static final class Builder {
        private Request mRequest;
        private int mCode = -1;
        private String mMessage;
        private Headers mHeader;
        private ByteArrayOutputStream mResponseBody;

        public Builder() {
            mHeader = new Headers();
        }

        public Builder setStatusCode(int code) {
            mCode = code;
            return this;
        }

        public Builder setRequest(Request request) {
            mRequest = request;
            return this;
        }

        public Builder setMessage(String message) {
            mMessage = message;
            return this;
        }

        public Builder addResponseHeader(String name, String value) {
            mHeader.setHeader(name, value);
            return this;
        }

        public Builder setResponseBody(ByteArrayOutputStream responseBody) {
            mResponseBody = responseBody;
            return this;
        }

        public Response build() {
            return new Response(this);
        }
    }

}
