package com.lpy.common.base.databinding;

import androidx.databinding.BindingAdapter;
import android.view.View;

/**
 * @author lipeiyong
 * @date on 2018/9/3 下午3:54
 */
public class VisibleDataBindingAdapter {

    @BindingAdapter("android:visibility")
    public static void setVisibility(View view, Boolean value) {
        view.setVisibility(value ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("visibleGone")
    public static void visibleGone(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("visibleInvisible")
    public static void visibleInvisible(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }
}
