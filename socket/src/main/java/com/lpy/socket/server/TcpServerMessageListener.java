package com.lpy.socket.server;

/**
 * @author lipeiyong
 * @since 2023/07/05 16:17
 */
public interface TcpServerMessageListener {

    /**
     * 收到客户端消息
     *
     * @param client 用户端对象
     * @param msg  消息
     */
    void onClientMessage(Client client, String msg);

    void onClientError(String sn,Throwable t);

}
