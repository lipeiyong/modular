package com.lpy.socket.client;


import android.util.Log;

import com.lpy.socket.TcpMessageBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;

import androidx.annotation.NonNull;

/**
 * 客户端
 *
 * @author lipeiyong
 * @since 2023/07/05 16:17
 */
public class TcpClient implements Tcp<TcpListener> {
    private static final String TAG = "TcpClient";
    private final int SERVER_PORT;
    private final String SERVER_IP;
    private final TcpListenerHelper<TcpListener> listenerHelper = new TcpListenerHelper<>();
    private Socket mSocket;
    private TcpWriteThread tcpWriteThread;
    private TcpReadThread tcpReadThread;

    public TcpClient(String ip, int port) {
        this.SERVER_IP = ip;
        this.SERVER_PORT = port;
    }

    @Override
    public void open() {
        try {
            if (null != mSocket) {
                throw new IllegalStateException("socket is not null or socket is not close !");
            }
            Log.i(TAG, "start open");
            SocketAddress serverSocketAddress = new InetSocketAddress(SERVER_IP, SERVER_PORT);
            mSocket = new Socket();
            mSocket.setKeepAlive(true);
            mSocket.setReceiveBufferSize(1024 * 8);
            mSocket.setPerformancePreferences(0, 1, 0);
            mSocket.setSendBufferSize(1024 * 2);
            mSocket.setSoTimeout(20 * 1000);
            mSocket.connect(serverSocketAddress, 8000);
            tcpWriteThread = new TcpWriteThread(mSocket.getOutputStream(), this::error);
            tcpReadThread = new TcpReadThread(mSocket.getInputStream(), this::error, listenerHelper::callMessage);
            tcpWriteThread.start();
            tcpReadThread.start();
            Log.i(TAG, String.format("open success ip[%s],port[%d]", SERVER_IP, SERVER_PORT));
            listenerHelper.callOpen();
        } catch (Exception e) {
            Log.e(TAG, String.format("open fail ip[%s],port[%d]", SERVER_IP, SERVER_PORT));
            error(e);
        }
    }

    private void error(Throwable e) {
        Log.e(TAG, String.format("error: is connect alive %b", isConnected()));
        listenerHelper.callError(e);
        close();
    }

    @Override
    public void close() {
        Log.i(TAG, String.format("close called ip[%s]，port[%d]", SERVER_IP, SERVER_PORT));
        if (null != mSocket && !mSocket.isClosed()) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mSocket = null;
        }
        if (null != tcpReadThread && tcpReadThread.isAlive()) {
            tcpReadThread.interrupt();
            tcpReadThread = null;
        }
        if (null != tcpWriteThread && tcpWriteThread.isAlive()) {
            tcpWriteThread.interrupt();
            tcpWriteThread = null;
        }
        listenerHelper.callClose();
    }

    @Override
    public boolean isConnected() {
        return null != mSocket && mSocket.isConnected();
    }

    @Override
    public void send(String msg) throws IllegalArgumentException, SocketException {
        if (null == msg || msg.length() == 0) {
            throw new IllegalArgumentException("msg length is 0 !");
        }
        if (null == mSocket) {
            throw new SocketException("socket not open");
        }
        if (!mSocket.isConnected()) {
            throw new SocketException("socket not connected");
        }
        tcpWriteThread.send(msg);
    }

    @Override
    public void registerListener(TcpListener tcpListener) {
        listenerHelper.registerListener(tcpListener);
    }

    @Override
    public void unRegisterListener(TcpListener tcpListener) {
        listenerHelper.unRegisterListener(tcpListener);
    }

    interface ThreadErrorListener {
        /**
         * Error
         *
         * @param e {@link Throwable}
         */
        void onError(Throwable e);
    }

    interface ThreadMessageListener {
        /**
         * Error
         *
         * @param message {@link TcpMessageBuffer#parse(byte[], int)}
         */
        void onMessage(String message);
    }

    static class TcpReadThread extends Thread {

        final TcpMessageBuffer readBuffer = new TcpMessageBuffer();
        final InputStream inputStream;
        final ThreadErrorListener threadErrorListener;
        final ThreadMessageListener threadMessageListener;

        TcpReadThread(@NonNull InputStream inputStream, @NonNull ThreadErrorListener threadErrorListener, @NonNull ThreadMessageListener threadMessageListener) {
            super("TcpReadThread");
            this.inputStream = inputStream;
            this.threadErrorListener = threadErrorListener;
            this.threadMessageListener = threadMessageListener;
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
                        throw new RuntimeException("stream read -1");
                    }
                    String[] messages = readBuffer.parse(buffer, read);
                    if (null != messages && messages.length > 0) {
                        String tempMsg = null;
                        for (String m : messages) {
                            if (null == tempMsg) {
                                tempMsg = m;
                            } else if (tempMsg.equals(m)) {
                                Log.w(TAG, String.format("Reject READ : %s ", tempMsg));
                                continue;
                            } else {
                                tempMsg = m;
                            }
                            Log.i(TAG, String.format("READ : %s ", tempMsg));
                            threadMessageListener.onMessage(tempMsg);
                        }
                    }
                } while (!isInterrupted());
            } catch (Exception e) {
                Log.w(TAG, e);
                threadErrorListener.onError(e);
            }
            Log.i(TAG, "TcpReadThread  close !!! ");
        }
    }

    static class TcpWriteThread extends Thread {

        final LinkedBlockingQueue<String> sendBuffer = new LinkedBlockingQueue<>(256);

        final OutputStream outputStream;
        final ThreadErrorListener threadErrorListener;

        TcpWriteThread(@NonNull OutputStream outputStream, @NonNull ThreadErrorListener threadErrorListener) {
            super("TcpWriteThread");
            this.outputStream = outputStream;
            this.threadErrorListener = threadErrorListener;
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
                    Log.d(TAG, String.format("SEND :: %s", take));
                    outputStream.write(take.getBytes());
                    outputStream.write('@');
                    outputStream.flush();
                } while (!isInterrupted());
            } catch (InterruptedException e) {
                Log.w(TAG, e);
            } catch (Exception e) {
                Log.w(TAG, e);
                threadErrorListener.onError(e);
            }
            Log.i(TAG, "TcpWriteThread close !!! ");
        }
    }
}
