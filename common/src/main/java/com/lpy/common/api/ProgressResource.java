package com.lpy.common.api;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;


/**
 * 用于{@link androidx.lifecycle.LiveData}对外暴露请求进度,比如下载文件
 * <p>
 * 如果仅需要知道是否处于请求中,请使用{@link Resource}
 * <p>
 * 一旦创建,数据不允许更改
 * 每次使用都会创建新的实例
 *
 * @author lipeiyong
 */
public final class ProgressResource<T> {


    public static final int PROGRESS_START = 0;
    public static final int PROGRESS_FINISH = -1;
    public static final int PROGRESS_ERROR = -2;
    public static final int PROGRESS_CANCEL = -3;

    /**
     * 进度
     * {@link #PROGRESS_START}表示开始
     * {@link #PROGRESS_FINISH}表示成功
     * {@link #PROGRESS_ERROR}表示失败
     */
    public final int progress;

    /**
     * 最大值
     */
    public final int max;

    /**
     * 只有{@link #progress} == {@link #PROGRESS_ERROR} 时才会此code才有效
     * <p>
     * 由数据源提供code 这里只是传递
     * <p>
     * eg: http.errorCode
     * </p>
     */
    public final int errorCode;
    /**
     * 数据
     */
    @Nullable
    public final T data;

    private ProgressResource(@Nullable T data, int progress, int max, int errorCode) {
        this.progress = progress;
        this.errorCode = errorCode;
        this.data = data;
        this.max = max;
    }

    public static <S> ProgressResource<S> progress(@Nullable S data, @IntRange(from = 0, to = Integer.MAX_VALUE) int progress) {
        return new ProgressResource<>(data, progress, 100, 0);
    }

    public static <S> ProgressResource<S> progress(@Nullable S data, @IntRange(from = 0, to = Integer.MAX_VALUE) int progress, int max) {
        if (progress > max) {
            throw new IllegalArgumentException("progress must <= max current progress = [" + progress + "] , max = [" + max + "]");
        }
        return new ProgressResource<>(data, progress, max, 0);
    }

    public static <S> ProgressResource<S> start(@Nullable S data) {
        return new ProgressResource<>(data, PROGRESS_START, 100, 0);
    }

    public static <S> ProgressResource<S> start(@Nullable S data, int max) {
        return new ProgressResource<>(data, PROGRESS_START, max, 0);
    }

    public static <S> ProgressResource<S> finish(@Nullable S data) {
        return new ProgressResource<>(data, PROGRESS_FINISH, 100, 0);
    }

    public static <S> ProgressResource<S> finish(@Nullable S data, int max) {
        return new ProgressResource<>(data, PROGRESS_FINISH, max, 0);
    }

    public static <S> ProgressResource<S> cancel(@Nullable S data) {
        return new ProgressResource<>(data, PROGRESS_CANCEL, 100, 0);
    }

    public static <S> ProgressResource<S> error(@Nullable S data, int errorCode) {
        if (errorCode == 0) {
            throw new IllegalArgumentException("errorCode can't be zero !");
        }
        return new ProgressResource<>(data, PROGRESS_ERROR, 100, errorCode);
    }

    public static <S> ProgressResource<S> error(@Nullable S data, int max, int errorCode) {
        if (errorCode == 0) {
            throw new IllegalArgumentException("errorCode can't be zero !");
        }
        return new ProgressResource<>(data, PROGRESS_ERROR, max, errorCode);
    }

    @Override
    public String toString() {
        return "ProgressResource{" +
                "progress=" + progress +
                ", max=" + max +
                ", data=" + data +
                '}';
    }

}
