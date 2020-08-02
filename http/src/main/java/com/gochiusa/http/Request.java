package com.gochiusa.http;


public final class Request {
    public static final String GET_METHOD = "GET";
    public static final String POST_METHOD = "POST";
    /**
     * 请求方法
     */
    String mRequestMethod;

    /**
     *  请求的内容，如果执行GET方法，则为null
     */
    RequestBody mRequestBody;

    /**
     *  想要向其发起请求的网址
     */
    String mHttpUrl;

    /**
     *  请求头
     */
    Headers mHeaders;

    Request(Builder builder) {
        this.mHeaders = builder.requestHeaders;
        this.mHttpUrl = builder.requestUrl;
        this.mRequestMethod = builder.requestMethod;
        this.mRequestBody = builder.requestBody;
    }

    /**
     * 建造者模式
     */
    public static final class Builder {
        private Headers requestHeaders;
        private String requestMethod;
        private RequestBody requestBody;
        private String requestUrl;

        public Builder() {
            requestHeaders = new Headers();
            // 默认使用GET请求
            requestMethod = GET_METHOD;
        }

        public Builder post(RequestBody requestBody) {
            methodWithBody(Request.POST_METHOD, requestBody);
            return this;
        }

        public Builder get() {
            methodWithBody(Request.GET_METHOD, null);
            return this;
        }

        public Builder setUrl(String url) {
            this.requestUrl = url;
            return this;
        }

        /**
         * 添加请求头
         */
        public Builder addHeader(String name, String value) {
            requestHeaders.setHeader(name, value);
            return this;
        }


        public Request build() {
            return new Request(this);
        }

        /**
         *  辅助方法，设置请求的方法和请求体
         */
        private void methodWithBody(String requestMethod, RequestBody requestBody) {
            this.requestMethod = requestMethod;
            this.requestBody = requestBody;
        }
    }

}
