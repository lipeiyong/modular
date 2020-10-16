package com.lpy.modularization;


import com.alibaba.android.arouter.launcher.ARouter;
import com.komlin.libcommon.util.SP_Utils;
import com.lpy.modularization.dagger.DaggerAppComponent;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import timber.log.Timber;

/**
 * @author lipeiyong
 */
public class CustomApplication extends DaggerApplication {

    private static CustomApplication mInstance;

    public static CustomApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        SP_Utils.init(this, "modularization");
        initLogs();
        ARouter.init(this);
        if (BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }
    }

    void initLogs() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree() {
                @Override
                protected void log(int priority, String tag, String message, Throwable t) {
                    super.log(priority, "Watch-" + tag, message, t);
                }
            });
        }
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().application(this).build();
    }
}