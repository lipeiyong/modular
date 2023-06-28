package com.lpy.common.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.lang.reflect.Method;

import timber.log.Timber;

/**
 * @author lipeiyong
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class TDevice {

    // 手机网络类型
    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;
    private static final String DEVICE_SN;
    public static boolean GTE_HC;
    public static boolean GTE_ICS;
    public static boolean PRE_HC;

    static {
        GTE_ICS = Build.VERSION.SDK_INT >= 14;
        GTE_HC = Build.VERSION.SDK_INT >= 11;
        PRE_HC = Build.VERSION.SDK_INT < 11;
    }

    static {
        String deviceCode = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            deviceCode = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            Timber.e(e.toString());
        } finally {
            DEVICE_SN = deviceCode;
        }
    }

    /**
     * 获取设备SN码
     *
     * @return
     */
    public static String getDeviceSnCode() {
        return DEVICE_SN;
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(
                Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(
                displaymetrics);
        return displaymetrics;
    }

    /**
     * 判断当前网络是否可用
     *
     * @return
     */
    public static boolean hasInternet(Context context) {
        boolean flag;
        //获取ConnectivityManager对象。
        ConnectivityManager mana = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //得到当前使用的网络
        NetworkInfo activeNetworkInfo = mana.getActiveNetworkInfo();
        flag = activeNetworkInfo != null;
        return flag;
    }

    public static boolean isWifiOpen(Context context) {
        boolean isWifiConnect = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // check the networkInfos numbers
        NetworkInfo[] networkInfos = cm.getAllNetworkInfo();
        for (int i = 0; i < networkInfos.length; i++) {
            if (networkInfos[i].getState() == NetworkInfo.State.CONNECTED) {
                if (networkInfos[i].getType() == ConnectivityManager.TYPE_MOBILE) {
                    isWifiConnect = false;
                }
                if (networkInfos[i].getType() == ConnectivityManager.TYPE_WIFI) {
                    isWifiConnect = true;
                }
            }
        }
        return isWifiConnect;
    }

    /**
     * 获取当前网络类型
     *
     * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
     */
    public static int getNetworkType(Context context) {
        int netType = 0;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if (!TextUtils.isEmpty(extraInfo)) {
                if (extraInfo.toLowerCase().equals("cmnet")) {
                    netType = NETTYPE_CMNET;
                } else {
                    netType = NETTYPE_CMWAP;
                }
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI;
        }
        return netType;
    }
}
