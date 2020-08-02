package com.gochiusa.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *  以键值对形式储存的请求头或者响应头
 */
public final class Headers {

    /**
     *  使用Map储存请求头的键值
     */
    private Map<String, String> mNameWithValue;

    Headers() {
        mNameWithValue = new HashMap<>();
    }

    public void setHeader(String name, String value) {
        mNameWithValue.put(name, value);
    }

    public String getValue(String name) {
        return mNameWithValue.get(name);
    }

    public Set<Map.Entry<String, String>> getEntrySets() {
        return mNameWithValue.entrySet();
    }
}
