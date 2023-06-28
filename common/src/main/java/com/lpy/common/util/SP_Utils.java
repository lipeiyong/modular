package com.lpy.common.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author lipeiyong
 */

public class SP_Utils {
    private static SharedPreferences sp;

    public static void init(Context context, String name) {
        if (null == SP_Utils.sp) {
            SP_Utils.sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        }
    }

    public static SharedPreferences instance() {
        return sp;
    }

    public static void saveInt(String key, int value) {
        sp.edit().putInt(key, value).apply();
    }

    public static int getInt(String key, int defaultValue) {
        return sp.getInt(key, defaultValue);
    }

    public static void saveString(String key, String value) {
        sp.edit().putString(key, value).apply();
    }

    public static String getString(String key, String defaultValue) {
        return sp.getString(key, defaultValue);
    }

    public static void saveBoolean(String key, boolean value) {
        sp.edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }

    public static void remove(String key) {
        sp.edit().remove(key).apply();
    }


    public static void saveObject(String key, Object value) {
        ObjectSaveUtil.saveObject(sp, key, value);
    }

    public static Object readObject(String key) {
        return ObjectSaveUtil.readObject(sp, key);
    }

}
