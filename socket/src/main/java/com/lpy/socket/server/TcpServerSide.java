package com.lpy.socket.server;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.lpy.socket.AppExecutors;
import com.lpy.socket.IpUtils;
import com.lpy.socket.TcpMessageBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 服务端
 *
 * @author lipeiyong
 * @since 2023/07/05 16:17
 */
public class TcpServerSide implements TcpServer<TcpServerListener> {
    private static final String TAG = "TcpServerSide";

    private final int PORT;
    private ServerSocket mServerSocket;
    private ServerThread mServerThread;
    private static final Map<String, Client> mClientList = new HashMap<>();
    private final TcpServerListenerHelper<TcpServerListener> listenerHelper = new TcpServerListenerHelper<>();
    private final Context mContext;

    public TcpServerSide(Context context, int port) {
        this.mContext = context;
        this.PORT = port;
    }

    /**
     * 开启
     */
    @Override
    public synchronized void start() {
        try {
            if (null != mServerSocket) {
                throw new IllegalStateException("ServerSocket is not null or socket is not close !");
            }
            mServerSocket = new ServerSocket(PORT);
            mServerSocket.setReceiveBufferSize(1024 * 8);
            mServerThread = new ServerThread(mServerSocket, listenerHelper);
            mServerThread.start();
            Log.i(TAG, String.format(Locale.CANADA, "ServerSocket start success. ip[%s],port[%d]", IpUtils.getIP(mContext), PORT));
            listenerHelper.callOpen();
        } catch (IOException e) {
            Log.i(TAG, "ServerSocket start fail! error msg " + e.getMessage());
            error(e);
        }
    }

    private void error(Throwable e) {
        Log.e(TAG, String.format("error: is start alive %b", isStarted()));
        listenerHelper.callError(e);
        if (null != mServerSocket && !mServerSocket.isClosed()) {
            close();
        }
    }

    /**
     * 关闭
     */
    @Override
    public synchronized void close() {
        Log.i(TAG, String.format(Locale.CANADA, "ServerSocket close called ip[%s],port[%d]", IpUtils.getIP(mContext), PORT));
        if (mServerSocket != null && !mServerSocket.isClosed()) {
            try {
                mServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mServerSocket = null;
        }
        if (mServerThread != null && mServerThread.isAlive()) {
            mServerThread.interrupt();
            mServerThread = null;
        }
        for (Client client : mClientList.values()) {
            if (null != client.socket && !client.socket.isClosed()) {
                try {
                    client.socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                client.socket = null;
            }
            if (null != client.tcpReadThread && client.tcpReadThread.isAlive()) {
                client.tcpReadThread.interrupt();
            }
            if (null != client.tcpWriteThread && client.tcpWriteThread.isAlive()) {
                client.tcpWriteThread.interrupt();
            }
        }
        mClientList.clear();

        listenerHelper.callClose();
    }

    public synchronized void closeClientAll() {
        Log.i(TAG, "closeClientAll");
        for (Client client : mClientList.values()) {
            if (client.regTimeoutSch != null) {
                client.regTimeoutSch.cancel(true);
            }
            if (null != client.socket && !client.socket.isClosed()) {
                try {
                    client.socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                client.socket = null;
            }
            if (null != client.tcpReadThread && client.tcpReadThread.isAlive()) {
                client.tcpReadThread.interrupt();
            }
            if (null != client.tcpWriteThread && client.tcpWriteThread.isAlive()) {
                client.tcpWriteThread.interrupt();
            }
        }
        mClientList.clear();
    }

    public synchronized void closeClient(String sn) {
        for (Client client : mClientList.values()) {
            if (client.sn != null && client.sn.equals(sn)) {
                if (client.regTimeoutSch != null) {
                    client.regTimeoutSch.cancel(true);
                }
                try {
                    client.socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                client.socket = null;
                if (null != client.tcpReadThread && client.tcpReadThread.isAlive()) {
                    client.tcpReadThread.interrupt();
                }
                if (null != client.tcpWriteThread && client.tcpWriteThread.isAlive()) {
                    client.tcpWriteThread.interrupt();
                }
                mClientList.remove(client.address);
                break;
            }
        }
    }

    public synchronized void closeClient(Client client) {
        if (client.regTimeoutSch != null) {
            client.regTimeoutSch.cancel(true);
        }
        try {
            client.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        client.socket = null;
        if (null != client.tcpReadThread && client.tcpReadThread.isAlive()) {
            client.tcpReadThread.interrupt();
        }
        if (null != client.tcpWriteThread && client.tcpWriteThread.isAlive()) {
            client.tcpWriteThread.interrupt();
        }
        mClientList.remove(client.address);
    }

    @Override
    public boolean isStarted() {
        return null != mServerSocket && !mServerSocket.isClosed();
    }

    @Override
    public void send(@NonNull String sn, String msg) throws IllegalArgumentException, SocketException {
        if (null == msg || msg.length() == 0) {
            throw new IllegalArgumentException("msg length is 0 !");
        }
        if (null == mServerSocket) {
            throw new SocketException("ServerSocket not open");
        }
        if (mServerSocket.isClosed()) {
            throw new SocketException("ServerSocket is closed");
        }
        for (Client client : mClientList.values()) {
            if (client.sn.equals(sn)) {
                client.tcpWriteThread.send(msg);
                break;
            }
        }
    }

    @Override
    public void registerListener(TcpServerListener tcpListener) {
        listenerHelper.registerListener(tcpListener);
    }

    @Override
    public void unRegisterListener(TcpServerListener tcpListener) {
        listenerHelper.unRegisterListener(tcpListener);
    }

    private static class ServerThread extends Thread {

        final ServerSocket serverSocket;
        final TcpServerListenerHelper<TcpServerListener> listenerHelper;

        public ServerThread(@NonNull ServerSocket serverSocket, @NonNull TcpServerListenerHelper<TcpServerListener> listenerHelper) {
            this.serverSocket = serverSocket;
            this.listenerHelper = listenerHelper;
        }

        @Override
        public void run() {
            super.run();
            try {
                do {
                    Log.i(TAG, "等待设备的连接... ... ");
                    final Socket socket = serverSocket.accept();
                    //设置读取超时时间20秒
                    socket.setSoTimeout(20 * 1000);
                    // 获取连接设备的地址及端口号
                    final String address = socket.getRemoteSocketAddress().toString();
                    Log.i(TAG, "连接成功，连接的设备为：" + address);

//                    InputStreamReader isr = new InputStreamReader(socket.getInputStream(), "gb2312");
                    TcpReadThread tcpReadThread = new TcpReadThread(socket.getInputStream(), this::error, this::msg);
                    tcpReadThread.setName(address);
                    TcpWriteThread tcpWriteThread = new TcpWriteThread(socket.getOutputStream(), this::error);
                    tcpWriteThread.setName(address);

                    Client client = new Client();
                    client.address = address;
                    client.socket = socket;
                    client.tcpReadThread = tcpReadThread;
                    client.tcpWriteThread = tcpWriteThread;

                    client.regTimeoutSch = AppExecutors.getInstance().scheduledExecutor().schedule(() -> {
                        error(address, new TimeoutException("client register timeout!"));
                    }, 6000, TimeUnit.MILLISECONDS);

                    // 放进到Map中保存
                    mClientList.put(address, client);

                    tcpReadThread.start();
                    tcpWriteThread.start();

                } while (!isInterrupted());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private synchronized void msg(String flag, String msg) {
            Client client = mClientList.get(flag);
            if (client == null) {
                return;
            }
            listenerHelper.callClientMessage(client, msg);
        }

        private void error(String flag, Throwable t) {
            Client client = mClientList.get(flag);
            if (client != null) {
                Socket socket = client.socket;
                TcpReadThread tcpReadThread = client.tcpReadThread;
                TcpWriteThread tcpWriteThread = client.tcpWriteThread;
                Log.e(TAG, String.format("error: is connect alive %b", socket != null && socket.isConnected()));
                if (null != socket && !socket.isClosed()) {
                    try {
                        socket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (null != tcpReadThread && tcpReadThread.isAlive()) {
                    tcpReadThread.interrupt();
                }
                if (null != tcpWriteThread && tcpWriteThread.isAlive()) {
                    tcpWriteThread.interrupt();
                }
                mClientList.remove(flag);
                if (client.sn != null) {
                    listenerHelper.callClientError(client.sn, t);
                }
            }
        }
    }

    public static class TcpReadThread extends Thread {

        final TcpMessageBuffer readBuffer = new TcpMessageBuffer();
        final InputStream inputStream;
        final ThreadErrorListener threadErrorListener;
        final ThreadMessageListener threadMessageListener;
        private String sn;

        public TcpReadThread(@NonNull InputStream inputStream, @NonNull ThreadErrorListener threadErrorListener, @NonNull ThreadMessageListener threadMessageListener) {
            this.inputStream = inputStream;
            this.threadErrorListener = threadErrorListener;
            this.threadMessageListener = threadMessageListener;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        @Override
        public void run() {
            super.run();
            try {
                byte[] buffer = new byte[1024 * 8];
                int read;
                do {
                    read = inputStream.read(buffer);
                    if (read == -1) {
                        throw new RuntimeException(String.format("sn=%s; stream read -1", sn));
                    }
                    String[] messages = readBuffer.parse(buffer, read);
                    if (null != messages && messages.length > 0) {
                        String tempMsg = null;
                        for (String m : messages) {
                            if (null == tempMsg) {
                                tempMsg = m;
                            } else if (tempMsg.equals(m)) {
                                Log.w(TAG, String.format("Reject READ : sn=%s; %s ", sn, tempMsg));
                                continue;
                            } else {
                                tempMsg = m;
                            }
                            Log.i(TAG, String.format("READ : sn=%s; %s ", sn, tempMsg));
                            threadMessageListener.onMessage(getName(), tempMsg);
                        }
                    }

                } while (!isInterrupted());
            } catch (Exception e) {
                Log.e(TAG, String.format("sn=%s; tcp read error ：%s", sn, e.getMessage()));
                threadErrorListener.onError(getName(), e);
            }
            Log.i(TAG, String.format("sn=%s; tcp read thread close !!!", sn));
        }
    }

    public static class TcpWriteThread extends Thread {

        final LinkedBlockingQueue<String> sendBuffer = new LinkedBlockingQueue<>(256);
        final OutputStream outputStream;
        final ThreadErrorListener threadErrorListener;
        private String sn;

        TcpWriteThread(@NonNull OutputStream outputStream, @NonNull ThreadErrorListener threadErrorListener) {
            super("TcpWriteThread");
            this.outputStream = outputStream;
            this.threadErrorListener = threadErrorListener;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        final void send(String msg) {
            sendBuffer.add(msg);
        }

        @Override
        public void run() {
            super.run();
            try {
                do {
                    String take = sendBuffer.take();
                    Log.i(TAG, String.format("SEND ::sn=%s; %s", sn, take));
                    outputStream.write(take.getBytes());
                    outputStream.write('@');
                    outputStream.flush();
                } while (!isInterrupted());
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                threadErrorListener.onError(getName(), e);
            }
            Log.i(TAG, String.format("sn=%s; TcpWriteThread close !!!", sn));
        }
    }

    interface ThreadErrorListener {
        /**
         * Error
         *
         * @param e {@link Throwable}
         */
        void onError(String flag, Throwable e);
    }

    interface ThreadMessageListener {
        /**
         * Error
         *
         * @param message {@link TcpMessageBuffer#parse(byte[], int)}
         */
        void onMessage(String flag, String message);
    }

}
