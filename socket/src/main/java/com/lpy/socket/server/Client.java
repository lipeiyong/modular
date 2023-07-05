package com.lpy.socket.server;

import java.net.Socket;
import java.util.concurrent.ScheduledFuture;

/**
 * 用户端类
 *
 * @author lipeiyong
 * @since 2023/07/05 16:17
 */
public class Client {
    /**
     * Remote Socket Address
     */
    public String address;
    /**
     * 接入端SN号
     */
    public String sn;
    public Socket socket;
    public TcpServerSide.TcpReadThread tcpReadThread;
    public TcpServerSide.TcpWriteThread tcpWriteThread;
    public ScheduledFuture<?> regTimeoutSch;
}
