package com.lpy.socket.client;

/**
 * @author lipeiyong
 * @since 2023/07/05 16:17
 */
public interface TcpMessageListener {

    /**
     * 收到服务器消息
     *
     * @param msg 消息
     */
    void onMessage(String msg);

}
