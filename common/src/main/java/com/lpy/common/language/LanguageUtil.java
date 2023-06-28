package com.lpy.common.language;

import android.annotation.SuppressLint;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Objects;

/**
 * 语言切换帮助类
 * <p>
 * @author lipeiyong
 * @date 2019/3/21 10:57
 */
public class LanguageUtil {

    /**
     * 判断当前语言是否是指定语言
     *
     * @param context
     * @param locale
     * @return
     */
    public static boolean isLocaleSameCurrentLocale(Context context, Locale locale) {
        Configuration config = new Configuration(context.getResources().getConfiguration());
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return Objects.equals(locale.toString(), config.locale.toString());
        } else {
            return Objects.equals(locale.toString(), config.getLocales().get(0).toString());
        }
    }

    /**
     * 设置语言
     *
     * @param context
     * @param locale
     * @param cls
     */
    public static void setLanguage(Context context, Locale locale, Class<?> cls) {
        updateLanguage(locale);
    }

    /**
     * 设置语言-通过反射（需要管理员权限）
     *
     * @param locale
     * @return
     */
    @SuppressLint("PrivateApi")
    @SuppressWarnings("unchecked")
    public static boolean updateLanguage(Locale locale) {
        try {
            Class classIActManager = Class.forName("android.app.IActivityManager");
            Class classActManager = Class.forName("android.app.ActivityManagerNative");
            Method methodGetDefault = classActManager.getDeclaredMethod("getDefault");
            Object iActManager = methodGetDefault.invoke(classActManager);
            Method methodGetConfig = classIActManager.getDeclaredMethod("getConfiguration");
            Configuration config = (Configuration) methodGetConfig.invoke(iActManager);
            config.locale = locale;
            Class clzConfig = Class.forName("android.content.res.Configuration");
            java.lang.reflect.Field userSetLocale = clzConfig.getField("userSetLocale");
            userSetLocale.set(config, true);
            Class[] clzParams = {Configuration.class};
            Method methodUpdateConfig = classIActManager.getDeclaredMethod("updateConfiguration", clzParams);
            methodUpdateConfig.invoke(iActManager, config);
            BackupManager.dataChanged("com.android.providers.settings");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
