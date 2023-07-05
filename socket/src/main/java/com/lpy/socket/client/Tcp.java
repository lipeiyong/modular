package com.lpy.socket.client;

import java.net.SocketException;

/**
 * @author lipeiyong
 * @since 2023/07/05 16:17
 */
public interface Tcp<T extends TcpMessageListener> {

    /**
     * 打开连接
     */
    void open();

    /**
     * 关闭连接
     */
    void close();


    /**
     * 是否已连接
     *
     * @return true 已连接
     */
    boolean isConnected();

    /**
     * 发生消息到服务器
     *
     * @param msg 消息
     */
    void send(String msg) throws IllegalArgumentException, SocketException;

    /**
     * 注册监听
     *
     * @param tcpListener {@link TcpListener}
     */
    void registerListener(T tcpListener);

    /**
     * 取消注册监听
     *
     * @param tcpListener {@link TcpListener}
     */
    void unRegisterListener(T tcpListener);

}
