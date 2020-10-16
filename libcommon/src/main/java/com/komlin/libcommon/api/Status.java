package com.komlin.libcommon.api;

/**
 * 访问网络中的状态
 *
 * @author lipeiyong
 */

public enum Status {
    /**
     * 正在加载中
     */
    LOADING,
    /**
     * 加载失败
     * <p>
     * 与{@link #SUCCESS}一定且仅回调一个,一次
     */
    ERROR,

    /**
     * 加载成功
     * <p>
     * 与{@link #ERROR}一定且仅回调一个,一次
     */
    SUCCESS

}
