package com.lpy.common.base;

import android.app.Application;

/**
 * @author lipeiyong
 * @date on 18-6-7  下午4:25
 */
public abstract class BaseAndroidRepo {

    private Application application;

    public BaseAndroidRepo(Application application) {
        this.application = application;
    }

    protected Application getApplication() {
        if (null == application) {
            throw new IllegalStateException("no application!");
        }
        return application;
    }
}
