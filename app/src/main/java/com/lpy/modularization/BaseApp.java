package com.lpy.modularization;


import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.lpy.common.util.SP_Utils;

import timber.log.Timber;

/**
 * @author lipeiyong
 */
public class BaseApp extends Application {

    private static BaseApp mInstance;

    public static BaseApp getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        SP_Utils.init(this, "modularization");
        initLogs();
        if (BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);
    }

    void initLogs() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree() {
                @Override
                protected void log(int priority, String tag, String message, Throwable t) {
                    super.log(priority, "modularization-" + tag, message, t);
                }
            });
        }
    }
}