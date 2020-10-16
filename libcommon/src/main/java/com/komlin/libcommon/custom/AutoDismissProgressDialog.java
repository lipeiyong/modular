package com.komlin.libcommon.custom;

import android.app.ProgressDialog;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.GenericLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;


import java.util.HashMap;

/**
 * @author lipeiyong
 * @date on 2018/8/2 下午1:47
 */
public class AutoDismissProgressDialog extends ProgressDialog {

    public AutoDismissProgressDialog(Context context) {
        super(context);
    }

    public AutoDismissProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    /*好像没必要用map*/
    private static HashMap<Class<?>, AutoDismissProgressDialog> cache = new HashMap<>();

    /**
     * auto dismiss
     */
    public static AutoDismissProgressDialog get(FragmentActivity activity) {
        AutoDismissProgressDialog autoDismissProgressDialog = cache.get(activity.getClass());
        if (null == autoDismissProgressDialog) {
            autoDismissProgressDialog = new AutoDismissProgressDialog(activity);
            cache.put(activity.getClass(), autoDismissProgressDialog);
            activity.getLifecycle().addObserver((GenericLifecycleObserver) (source, event) -> {
                if (Lifecycle.Event.ON_DESTROY == event) {
                    AutoDismissProgressDialog progressBar = cache.remove(source.getClass());
                    if (null != progressBar && progressBar.isShowing()) {
                        progressBar.dismiss();
                    }
                }
            });
        }
        return autoDismissProgressDialog;
    }

    public void show(boolean cancelAble, String message) {
        setCancelable(cancelAble);
        setMessage(message);
        show();
    }

    public void show(String message) {
        setMessage(message);
        show();
        setCanceledOnTouchOutside(false);
    }

    public void dismiss(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        dismiss();
    }
}
