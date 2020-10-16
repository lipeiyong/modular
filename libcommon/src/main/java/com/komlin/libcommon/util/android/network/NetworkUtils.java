package com.komlin.libcommon.util.android.network;

import android.telephony.TelephonyManager;

/**
 * 网络帮助类
 * @author lipeiyong
 * @date 2019-11-24 20:03
 */
public class NetworkUtils {

    /**
     * 获取网络类别
     * @param networkType the NETWORK_TYPE_xxxx for current data connection.
     * @return
     */
    public static String getNetworkTypeName(int networkType){
        String ntype="";
        switch(networkType){
            case TelephonyManager.NETWORK_TYPE_GPRS:
                ntype="GPRS";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                ntype="EVDO_0";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                ntype="EVDO_A";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                ntype="EVDO_B";
                break;
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                ntype="EHRPD";
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
                ntype="UMTS";
                break;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                ntype="HSDPA";
                break;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                ntype="HSUPA";
                break;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                ntype="HSPA";
                break;
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                ntype="HSPAP";
                break;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                ntype="EDGE";
                break;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                ntype="CDMA";
                break;
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                ntype="1X";
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                ntype="LTE";
                break;
            default:
                ntype="无服务";
                break;
        }

        return ntype;
    }
}
