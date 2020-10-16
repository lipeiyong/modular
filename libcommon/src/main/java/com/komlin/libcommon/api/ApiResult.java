package com.komlin.libcommon.api;

import okhttp3.Headers;

/**
 * @author lipeiyong
 */
public class ApiResult<T> {

    public int code;
    public String msg;
    public T data;

    public final boolean isSuccessful() {
        return code == 0;
    }

    public final String errorMsg() {
        return null == msg ? "UnKnow Error!" : msg;
    }

    @Override
    public String toString() {
        return "ApiResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", date=" + data +
                '}';
    }
}
