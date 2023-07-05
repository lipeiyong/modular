package com.lpy.socket.client;


import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

import com.lpy.socket.AppExecutors;
import com.lpy.socket.BinaryExponentialBackHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 * @author lipeiyong
 * @since 2023/07/05 16:17
 */
public class AutoConnectTcpClient implements Tcp<TcpMessageListener> {
    private static final String TAG = "AutoConnectTcpClient";
    public static final String CONNECT_STATUS_OPEN = "open";
    public static final String CONNECT_STATUS_REGISTERED = "registered";
    public static final String CONNECT_STATUS_CLOSE = "close";
    private final TcpListenerHelper<TcpMessageListener> listenerHelper = new TcpListenerHelper<>();
    private final BinaryExponentialBackHelper binaryExponentialBackHelper = new BinaryExponentialBackHelper();
    private final TcpListener tcpListener = new MyListener();
    private final AppExecutors appExecutors;
    private ScheduledFuture<?> reConnTask;
    private ScheduledFuture<?> beatTask;
    private TcpClient tcp;
    private String ip;
    private int port;
    private String token;
    private TcpConnectListener tcpConnectListener;
    /**
     * 重新连接是否开启固定间隔
     */
    private boolean enable;
    /**
     * 重新连接间隔
     */
    private int delay;

    public AutoConnectTcpClient(AppExecutors appExecutors, String ip, int port) {
        this.appExecutors = appExecutors;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void open() {
        appExecutors.networkIO().execute(() -> {
            if (null != tcp) {
                close();
            }
            tcp = new TcpClient(ip, port);
            tcp.registerListener(tcpListener);
            tcp.open();
        });
    }

    /**
     * 是否开启固定间隔连接
     *
     * @param enable true=开启 false=关闭
     * @param delay  延迟时间，需>=0
     */
    public void enable(boolean enable, int delay) {
        this.enable = enable;
        this.delay = delay;
    }

    public void reOpen(String ip, int port) {
        //判断数据是否变更
        if (!ip.equals(this.ip) || port != this.port) {
            //判断是否已经打开
            if (tcp != null) {
                //先关闭
                close();
            }
            this.ip = ip;
            this.port = port;
            open();
        }
    }

    @Override
    public void close() {
        if (null != reConnTask && !reConnTask.isCancelled()) {
            reConnTask.cancel(true);
        }
        if (tcp != null) {
            tcp.unRegisterListener(tcpListener);
            tcp.close();
            Log.i(TAG, "socket close!");
        }
        stopBeatTask();
        updateConnectStatus(CONNECT_STATUS_CLOSE);
    }

    private void updateConnectStatus(String status) {
        if (tcpConnectListener != null) {
            tcpConnectListener.connectStatus(status);
        }
    }

    public String getToken() {
        return token;
    }

    public void setTcpConnectListener(TcpConnectListener tcpConnectListener) {
        this.tcpConnectListener = tcpConnectListener;
    }

    @Override
    public boolean isConnected() {
        return null != tcp && tcp.isConnected();
    }

    @Override
    public void send(String msg) {
        try {
            if (null != tcp && tcp.isConnected()) {
                tcp.send(msg);
            }
        } catch (SocketException e) {
            Log.w(TAG, e);
        }
    }

    @Override
    public void registerListener(TcpMessageListener tcpListener) {
        listenerHelper.registerListener(tcpListener);
    }

    @Override
    public void unRegisterListener(TcpMessageListener tcpListener) {
        listenerHelper.registerListener(tcpListener);
    }

    private void reConnectServer(int delay) {
        Log.w(TAG, String.format("called with: delay() called with: delay = [%d]", delay));
        if (null != reConnTask && !reConnTask.isCancelled()) {
            reConnTask.cancel(true);
        }
        reConnTask = appExecutors.scheduledExecutor().schedule(() -> tcp.open(), delay, TimeUnit.MILLISECONDS);
    }

    @SuppressLint("HardwareIds")
    private void registerToServer() {
        try {
            JSONObject registerJson = new JSONObject();
            registerJson.put("type", "REGISTER");
            String serial = Build.SERIAL;
            if (serial != null) {
                //变为大写
                serial = serial.replace(":", "").toUpperCase();
            }
            registerJson.put("sn", serial);
            send(registerJson.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("HardwareIds")
    public void beatToServer() {
        try {
            JSONObject beatJson = new JSONObject();
            beatJson.put("type", "HEART");
            beatJson.put("token", token);
            send(beatJson.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void startBeatTask() {
        beatTask = appExecutors.scheduledExecutor().scheduleWithFixedDelay(this::beatToServer, 8000, 8000, TimeUnit.MILLISECONDS);
    }

    private void stopBeatTask() {
        if (beatTask != null && !beatTask.isCancelled()) {
            beatTask.cancel(true);
        }
    }

    class MyListener implements TcpListener {

        @Override
        public void onOpen() {
            Log.w(TAG, "Tcp onOpen: !!!");
            updateConnectStatus(CONNECT_STATUS_OPEN);
            registerToServer();
            binaryExponentialBackHelper.clear();
            startBeatTask();
            listenerHelper.callOpen();
        }

        @Override
        public void onClose() {
            Log.w(TAG, "Tcp onClose:!!!");
            updateConnectStatus(CONNECT_STATUS_CLOSE);
            int next;
            if (enable && delay > 0) {
                next = delay;
            } else {
                next = binaryExponentialBackHelper.next() * 100;
            }
            reConnectServer(next);
            stopBeatTask();
            listenerHelper.callClose();
        }


        @Override
        public void onError(Throwable t) {
            //do nothing
        }

        @Override
        public void onMessage(String msg) {
            try {
                JSONObject jsonObject = new JSONObject(msg);
                String type = jsonObject.getString("type");
                if ("RE_REGISTER".equals(type)) {
                    token = jsonObject.getString("token");
                    updateConnectStatus(CONNECT_STATUS_REGISTERED);
                } else if ("RE_HEART".equals(type)) {
                    //do nothing
                } else {
                    listenerHelper.callMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
