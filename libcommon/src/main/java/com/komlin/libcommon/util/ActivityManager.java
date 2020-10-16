package com.komlin.libcommon.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

/**
 * @author lipeiyong
 */
public class ActivityManager {
    private static volatile ActivityManager instance;
    private ArrayList<Activity> activitySet = new ArrayList<>();

    private ActivityManager() {
    }

    /**
     * 单一实例
     */
    public static ActivityManager getInstance() {
        if (instance == null) {
            instance = new ActivityManager();
        }
        return instance;
    }

    /**
     * 获取指定的Activity
     *
     * @author kymjs
     */
    public Activity getActivity(Class<?> cls) {
        for (Activity activity : activitySet) {
            if (activity.getClass().equals(cls)) {
                return activity;
            }
        }
        return null;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        activitySet.add(activity);
    }

    /**
     * 将Activity移除堆栈
     */
    public void removeActivity(Activity activity) {
        activitySet.remove(activity);
    }

    /**
     * 结束所有Activity
     *
     * @param cls 保留activity
     */
    public void clearActivity(Class<?> cls) {
        Iterator<Activity> iterator = activitySet.iterator();
        while (iterator.hasNext()) {
            Activity next = iterator.next();
            if (cls.getSimpleName().equals(next.getClass().getSimpleName())) {
                continue;
            }
            next.finish();
            iterator.remove();
        }
    }

    public void clearAll() {
        Iterator<Activity> iterator = activitySet.iterator();
        while (iterator.hasNext()) {
            Activity next = iterator.next();
            next.finish();
            iterator.remove();
        }
    }

    public void starWithClearTask(Class<? extends Activity> clz, Bundle bundle) {
        if (activitySet.size() == 0) {
            return;
        }
        Activity last = activitySet.get(activitySet.size() - 1);
        if (Objects.equals(last.getClass(), clz)) {
            return;
        }
        clearAll();
        Intent intent = new Intent(last, clz);
        if (null != bundle) {
            bundle.putAll(bundle);
        }
        last.startActivity(intent);
    }
}
