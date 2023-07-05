package com.lpy.socket.client;

/**
 * @author lipeiyong
 * @since 2023/07/05 16:17
 */
public interface TcpListener extends TcpMessageListener {
    /**
     * Tcp 打开
     */
    void onOpen();

    /**
     * Tcp 关闭
     */
    void onClose();

    /**
     * Tcp Error
     *
     * @param t error
     */
    void onError(Throwable t);

}
