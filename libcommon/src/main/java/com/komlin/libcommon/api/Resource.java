package com.komlin.libcommon.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.komlin.libcommon.api.Status.ERROR;
import static com.komlin.libcommon.api.Status.LOADING;
import static com.komlin.libcommon.api.Status.SUCCESS;


/**
 * 用于{@link androidx.lifecycle.LiveData}对外暴露请求状态,比如web请求
 * <p>
 * 如果需要知道详细进度,请使用{@link ProgressResource}
 * <p>
 * 一旦创建,数据不允许更改
 * 每次使用都会创建新的实例
 *
 * @author lipeiyong
 */
public final class Resource<T>{
    @NonNull
    public final Status status;
    @Nullable
    public final T data;
    public final int errorCode;
    @Nullable
    public final String errorMessage;

    private Resource(@NonNull Status status, @Nullable T data, @Nullable int errorCode, String errorMessage) {
        this.status = status;
        this.data = data;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static <T> Resource<T> success(@NonNull T data) {
        return new Resource<>(SUCCESS, data, 0, null);
    }

    public static <T> Resource<T> error(int errorCode, String errorMessage, @Nullable T data) {
        return new Resource<>(ERROR, data, errorCode, errorMessage);
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(LOADING, data, 0, null);
    }

    public boolean isSuccess() {
        return status == SUCCESS;
    }

    public boolean isError() {
        return status == ERROR;
    }

    public boolean isLoading() {
        return status == LOADING;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "status=" + status +
                ", date=" + data +
                ", errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
