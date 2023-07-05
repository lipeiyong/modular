package com.lpy.socket.server;

/**
 * @author lipeiyong
 * @since 2023/07/05 16:17
 */
public interface TcpServerListener extends TcpServerMessageListener {
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
