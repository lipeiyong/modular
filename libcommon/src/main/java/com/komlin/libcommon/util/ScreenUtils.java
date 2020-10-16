package com.komlin.libcommon.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.WindowManager;

/**
 * 屏幕帮助类
 * 所需权限：
 * 屏幕亮度调整：
 * <uses-permission android:name="android.permission.CHANGE_CONFIGURATION"/>
 * <uses-permission android:name="android.permission.WRITE_SETTINGS"/>
 *
 * @author lipeiyong
 * @date 2019/11/16
 */
public class ScreenUtils {

    /**
     * 判断是否开启了自动亮度调节
     *
     * @param context
     * @return
     */
    public static boolean isAutoBrightness(Context context) {
        ContentResolver resolver = context.getContentResolver();
        boolean automicBrightness = false;
        try {
            automicBrightness = Settings.System.getInt(resolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return automicBrightness;
    }

    /**
     * 获取当前屏幕亮度
     *
     * @param context
     * @return
     */
    public static int getScreenBrightness(Context context) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = context.getContentResolver();
        try {
            nowBrightnessValue = android.provider.Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }

    /**
     * 关闭自动亮度调节
     *
     * @param activity
     * @param flag
     * @return
     */
    public static boolean autoBrightness(Context activity, boolean flag) {
        int value = 0;
        if (flag) {
            value = Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC; //开启
        } else {
            value = Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;//关闭
        }
        return Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                value);
    }

    /**
     * 设置亮度，退出app也能保持该亮度值
     *
     * @param context
     * @param brightness
     */
    public static void saveBrightness(Context context, int brightness) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = android.provider.Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
        android.provider.Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
        resolver.notifyChange(uri, null);
    }

    /**
     * 设置当前activity显示的亮度
     *
     * @param activity
     * @param brightness
     */
    public static void setBrightness(Activity activity, int brightness) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
        activity.getWindow().setAttributes(lp);
    }

    /**
     * 获取屏幕是否亮着
     *
     * @param context
     * @return true 是
     */
    public static boolean getScreenState(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return pm.isScreenOn();
    }
}
