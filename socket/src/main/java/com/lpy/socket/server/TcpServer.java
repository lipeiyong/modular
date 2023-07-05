package com.lpy.socket.server;

import java.net.SocketException;

/**
 * @author lipeiyong
 * @since 2023/07/05 16:17
 */
public interface TcpServer<T extends TcpServerMessageListener> {

    /**
     * 开启
     */
    void start();

    /**
     * 关闭
     */
    void close();


    /**
     * 是否已启动
     *
     * @return true 已连接
     */
    boolean isStarted();

    /**
     * 发送消息
     *
     * @param sn  设备序列号
     * @param msg 消息
     */
    void send(String sn, String msg) throws IllegalArgumentException, SocketException;

    /**
     * 注册监听
     *
     * @param tcpListener {@link TcpServerListener}
     */
    void registerListener(T tcpListener);

    /**
     * 取消注册监听
     *
     * @param tcpListener {@link TcpServerListener}
     */
    void unRegisterListener(T tcpListener);

}
