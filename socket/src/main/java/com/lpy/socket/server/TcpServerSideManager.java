package com.lpy.socket.server;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * @author lipeiyong
 * @since 2023/07/05 16:17
 */
public class TcpServerSideManager implements TcpServer<TcpServerMessageListener> {
    private static final String TAG = "TcpServerSideManager";
    private volatile static TcpServerSideManager mInstance;

    private TcpServerSide tcpServerSide;
    private int port;
    private Application mContext;
    /**
     * 服务端添加的客户端列表（key=sn,value="设备状态标记"）
     */
    private final MutableLiveData<Map<String, String>> clientListLiveData = new MutableLiveData<>();

    private final TcpServerListenerHelper<TcpServerMessageListener> listenerHelper = new TcpServerListenerHelper<>();

    public static TcpServerSideManager getInstance() {
        if (mInstance == null) {
            synchronized (TcpServerSideManager.class) {
                if (mInstance == null) {
                    mInstance = new TcpServerSideManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化
     *
     * @param port 服务端口
     */
    public void init(Application context, int port) {
        try {
            if (null != tcpServerSide) {
                throw new IllegalStateException("TcpServerSide is not null or socket is not close !");
            }
            this.mContext = context;
            this.port = port;

            start();
        } catch (Exception e) {
            Log.e(TAG, "ServerSocket start fail! error msg " + e.getMessage());
        }
    }

    /**
     * 更新设备列表(服务端添加的客户端列表)
     */
    public void updateClientList(List<String> newList) {
        Map<String, String> oldList = clientListLiveData.getValue();
        //判断是否清空所有列表
        if (newList == null || newList.size() == 0) {
            if (oldList != null && oldList.size() > 0) {
                //关闭所有终端
                if (tcpServerSide != null) {
                    tcpServerSide.closeClientAll();
                }
            }
            return;
        }
        //判断是否全部新增
        if (oldList == null || oldList.size() == 0) {
            //新增
            Map<String, String> addList = new HashMap<>();
            for (String sn : newList) {
                //默认处于离线
                addList.put(sn, "0");
            }
            clientListLiveData.setValue(addList);
            return;
        }

        //新增
        List<String> addList = new ArrayList<>();
        //删除
        List<String> delList = new ArrayList<>();
        for (String sn : newList) {
            boolean exist = oldList.containsKey(sn);
            if (!exist) {
                //不存在,则新增
                addList.add(sn);
            }
        }
        for (String sn : oldList.keySet()) {
            boolean exist = newList.contains(sn);
            if (!exist) {
                delList.add(sn);
            }
        }
        //删除
        for (String sn : delList) {
            oldList.remove(sn);
            if (tcpServerSide != null) {
                tcpServerSide.closeClient(sn);
            }
        }
        //新增
        for (String sn : addList) {
            oldList.put(sn, "0");
        }
        clientListLiveData.setValue(oldList);
    }

    public void addClient(String sn) {
        Map<String, String> clientList = clientListLiveData.getValue();
        if (clientList == null) {
            clientList = new HashMap<>();
        }
        clientList.put(sn, "0");
        clientListLiveData.setValue(clientList);
    }

    public void removeClient(String sn) {
        Map<String, String> clientList = clientListLiveData.getValue();
        if (clientList != null && clientList.size() > 0) {
            String rlt = clientList.remove(sn);
            if (rlt != null) {
                //删除成功
                if (tcpServerSide != null) {
                    tcpServerSide.closeClient(sn);
                }
                clientListLiveData.setValue(clientList);
            }
        }
    }

    public LiveData<Map<String, String>> getClientListLiveData() {
        return clientListLiveData;
    }

    @Override
    public void start() {
        try {
            if (null != tcpServerSide) {
                throw new IllegalStateException("TcpServerSide is not null or socket is not close !");
            }
            tcpServerSide = new TcpServerSide(mContext, port);
            tcpServerSide.registerListener(myListener);
            tcpServerSide.start();
        } catch (Exception e) {
            Log.e(TAG, "ServerSocket start fail! error msg " + e.getMessage());
        }
    }

    @Override
    public void close() {
        if (tcpServerSide != null) {
            tcpServerSide.unRegisterListener(myListener);
            tcpServerSide.close();
            tcpServerSide = null;
        }
    }

    @Override
    public boolean isStarted() {
        return tcpServerSide != null && tcpServerSide.isStarted();
    }

    @Override
    public void send(String sn, String msg) throws IllegalArgumentException, SocketException {
        if (tcpServerSide != null) {
            tcpServerSide.send(sn, msg);
        }
    }

    /**
     * 回复注册请求
     *
     * @param client 用户端对象
     */
    private synchronized void replyRegister(Client client) {
        try {
            UUID uuid = UUID.randomUUID();
            JSONObject registerJson = new JSONObject();
            registerJson.put("type", "RE_REGISTER");
            registerJson.put("token", uuid.toString());
            send(client.sn, registerJson.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 回复心跳
     *
     * @param client 用户端对象
     */
    private synchronized void replyHeart(Client client) {
        if (TextUtils.isEmpty(client.sn)) {
            return;
        }
        try {
            JSONObject beatJson = new JSONObject();
            beatJson.put("type", "RE_HEART");
            send(client.sn, beatJson.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerListener(TcpServerMessageListener tcpListener) {
        listenerHelper.registerListener(tcpListener);
    }

    @Override
    public void unRegisterListener(TcpServerMessageListener tcpListener) {
        listenerHelper.unRegisterListener(tcpListener);
    }

    private final MyListener myListener = new MyListener();


    class MyListener implements TcpServerListener {

        @Override
        public void onOpen() {
            Log.w(TAG, "Tcp server onOpen: !!!");
            listenerHelper.callOpen();
        }

        @Override
        public void onClose() {
            Log.w(TAG, "Tcp server onClose:!!!");
            listenerHelper.callClose();
        }


        @Override
        public void onError(Throwable t) {
            //do nothing
        }

        @Override
        public void onClientMessage(Client client, String msg) {
            try {
                JSONObject jsonObject = new JSONObject(msg);
                String type = jsonObject.getString("type");
                if ("REGISTER".equals(type)) {
                    //判断是否允许注册
                    String sn = jsonObject.getString("sn");
                    Map<String, String> clientList = clientListLiveData.getValue();
                    if (clientList == null || clientList.size() == 0) {
                        //本地列表为空,直接关闭
                        tcpServerSide.closeClient(client);
                        return;
                    }
                    //判断是否存在
                    boolean exist = clientList.containsKey(sn);
                    if (!exist) {
                        //不存在,直接关闭
                        tcpServerSide.closeClient(client);
                        return;
                    }
                    //存在,取消超时处理
                    client.regTimeoutSch.cancel(true);
                    client.sn = sn;
                    client.tcpReadThread.setSn(sn);
                    client.tcpWriteThread.setSn(sn);
                    //标记为在线
                    clientList.put(sn, "1");
                    clientListLiveData.postValue(clientList);
                    //回复
                    replyRegister(client);
                } else if ("HEART".equals(type)) {
                    replyHeart(client);
                } else {
                    //判断是否注册
                    if (TextUtils.isEmpty(client.sn)) {
                        Log.w(TAG, "未注册! flag=" + client.address);
                        return;
                    }
                    //token校验

                    listenerHelper.callClientMessage(client, msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClientError(String sn, Throwable t) {
            Map<String, String> clientList = clientListLiveData.getValue();
            if (clientList != null && clientList.size() > 0) {
                boolean exist = clientList.containsKey(sn);
                if (exist) {
                    //离线
                    clientList.put(sn, "0");
                    clientListLiveData.postValue(clientList);
                }
            }
        }
    }
}