package com.gochiusa.http;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * 类的设计模仿自OkHttp的Call
 */
public final class RealCall implements Call {

    /**
     *  发起请求的客户端
     */
    private HttpClient mClient;
    /**
     *  发起的请求
     */
    private Request mRequest;

    protected RealCall(HttpClient httpClient, Request request) {
        mClient = httpClient;
        mRequest = request;
    }


    @Override
    public Request request() {
        return mRequest;
    }

    /**
     *  发起同步请求，这会阻塞当前线程，勿在主线程进行
     * @return http请求获得的响应
     */
    @Override
    public Response execute() throws IOException {
        // 建立与服务器的连接
        HttpURLConnection connection = buildConnect(mRequest, mClient);
        // 返回相应的响应
        return createResponse(connection, mRequest);
    }

    /**
     *  进行Http连接
     * @param request 请求类，用于读取连接所需的信息
     * @param httpClient 储存了Http的连接设置，如是否使用缓存等
     * @return 初始化完毕，可以开始进行连接的{@code HttpURLConnection}
     */
    private HttpURLConnection buildConnect(Request request, HttpClient httpClient)
            throws IOException {
        // 先获取URL
        URL url = new URL(request.mHttpUrl);
        // 打开Http连接类
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        // 初始化设置
        initConnection(urlConnection, httpClient);
        // 设置请求方法
        urlConnection.setRequestMethod(request.mRequestMethod);
        // 添加请求头
        for (Map.Entry<String, String> entrySet : request.mHeaders.getEntrySets()) {
            urlConnection.setRequestProperty(entrySet.getKey(), entrySet.getValue());
        }
        if (request.mRequestMethod.equals(Request.POST_METHOD)) {
            // 覆盖是否允许输出的设置
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            // 添加POST请求参数
            addPostBody(request.mRequestBody, urlConnection.getOutputStream());
        } else {
            urlConnection.connect();
        }
        return urlConnection;
    }

    /**
     *  使用预置的连接设置来初始化{@code HttpURLConnection}
     * @param httpClient 带有用户期望的连接设置信息的类
     */
    private void initConnection(HttpURLConnection urlConnection, HttpClient httpClient) {
        // 设置连接最大超时
        urlConnection.setConnectTimeout(httpClient.mConnectionTimeout);
        // 设置读取数据最大超时
        urlConnection.setReadTimeout(httpClient.mReadTimeout);
        // 设置是否允许输入
        urlConnection.setDoInput(httpClient.mDoInput);
        // 是否允许允许输出
        urlConnection.setDoOutput(httpClient.mDoOutPut);
        // 设置是否使用缓存
        urlConnection.setUseCaches(httpClient.mUseCache);
    }

    /**
     *  向网站的输出流添加POST请求体
     * @param requestBody 包含了POST请求的信息
     * @param outputStream 从Http连接中获取到的网站的输出流，用于提交POST请求
     */
    private void addPostBody(RequestBody requestBody, OutputStream outputStream)
            throws IOException {
        requestBody.writeTo(outputStream);
    }

    /**
     *  根据网站的响应信息，填充相关的响应信息类
     * @param urlConnection 包含网站返回的响应信息
     * @param request 与这次的响应相对应的请求{@code Request}
     * @return 包含这次连接的响应数据的类
     */
    private Response createResponse(HttpURLConnection urlConnection, Request request)
            throws IOException {
        // 初始化建造者
        Response.Builder builder = new Response.Builder();
        // 设置请求信息、响应码、响应信息、响应头信息和响应体。
        builder.setRequest(request).setStatusCode(urlConnection.getResponseCode())
                .setMessage(urlConnection.getResponseMessage())
                .addResponseHeader(Response.CONTENT_TYPE, urlConnection.getContentType())
                .addResponseHeader(Response.CONTENT_ENCODING, urlConnection.getContentEncoding())
                .setResponseBody(buildResponseBody(urlConnection.getInputStream()));
        urlConnection.disconnect();
        return builder.build();
    }


    /**
     *   将输入流包含的数据转移到输出流{@code ByteArrayOutputSteam}
     * @param inputStream 储存响应信息的输入流
     * @return 写入服务器响应信息的{@code ByteArrayOutputSteam}
     * @throws IOException 读取数据可能出现异常
     */
    private static ByteArrayOutputStream buildResponseBody(InputStream inputStream)
            throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 缓存读取的字节数大小
        int byteCount;
        // 用作缓存区的字节数组
        byte[] bytes = new byte[1024];
        // 写入数据
        while ((byteCount = inputStream.read(bytes)) > 0) {
            byteArrayOutputStream.write(bytes, 0, byteCount);
        }
        return byteArrayOutputStream;
    }
}
