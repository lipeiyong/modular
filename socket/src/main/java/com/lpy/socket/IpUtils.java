package com.lpy.socket;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author lipeiyong
 * @since 2023/07/05 16:17
 */
public class IpUtils {

    /*** 获取IP* * @param context* @return*/
    public static String getIP(Context context) {
        String ip = "0.0.0.0";
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null) {
            int type = info.getType();
            if (type == ConnectivityManager.TYPE_ETHERNET) {
                ip = getEtherNetIP();
            } else if (type == ConnectivityManager.TYPE_WIFI) {
                ip = getWifiIP(context);
            }
            return ip;
        } else {
            return null;
        }
    }

    /*** 获取有线地址* * @return*/
    public static String getEtherNetIP() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("WifiPreference IpAddress", ex.toString());
        }
        return "0.0.0.0";
    }

    /*** 获取wifiIP地址* * @param context* @return*/
    public static String getWifiIP(Context context) {
        android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiinfo = wifi.getConnectionInfo();
        int intaddr = wifiinfo.getIpAddress();
        byte[] byteaddr = new byte[]{(byte) (intaddr & 0xff), (byte) (intaddr >> 8 & 0xff), (byte) (intaddr >> 16 & 0xff), (byte) (intaddr >> 24 & 0xff)};
        InetAddress addr = null;
        try {
            addr = InetAddress.getByAddress(byteaddr);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        String mobileIp = addr.getHostAddress();
        return mobileIp;
    }
}
