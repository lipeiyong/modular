package com.lpy.common.util.android;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import androidx.annotation.ColorInt;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author lipeiyong
 * @date on 2018/7/24 上午10:19
 */
public class SystemUIHelper {

    public static void setFullWindow(Window window) {
        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarColor(Window window, @ColorInt int color) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void setStatusBarTextColor(Window window, boolean lightStatusBar) {
        if (window == null) return;
        View decor = window.getDecorView();
        int ui = decor.getSystemUiVisibility();
        if (lightStatusBar) {
            ui |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        } else {
            ui &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        decor.setSystemUiVisibility(ui);
    }

    /**
     * 当xml中无法使用fitSystemWindow属性时
     */
    public static void fitSystemWindow(View toolbar) {
        int statusBarSize = getStatusBarHeight(toolbar.getContext());
        int paddingTop = toolbar.getPaddingTop();
        if (paddingTop != statusBarSize) {
            toolbar.setPadding(0, statusBarSize, 0, 0);
            toolbar.setBottom(statusBarSize + toolbar.getBottom());
            toolbar.requestLayout();
        }
        //menu.clear();

    }

    private static int statusBarHeight;
    private static int actionBarHeight;


    public static int getStatusBarHeight(View view) {
        if (view.isInEditMode()) {
            return 72;
        }
        return getStatusBarHeight(view.getContext());
    }

    public static int getStatusBarHeight(Context context) {
        if (statusBarHeight == 0) {
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
            }
        }
        return statusBarHeight;
    }

    public static int getActionBarHeight(View view) {
        if (view.isInEditMode()) {
            return 168;
        }
        return getActionBarHeight(view.getContext());
    }

    public static int getActionBarHeight(Context context) {
        if (actionBarHeight != 0) {
            return actionBarHeight;
        }
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true);
        int[] attribute = new int[]{android.R.attr.actionBarSize};
        TypedArray array = context.obtainStyledAttributes(typedValue.resourceId, attribute);
        actionBarHeight = array.getDimensionPixelSize(0, -1);
        array.recycle();
        return actionBarHeight;
    }
}
