package com.lpy.common.base;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lpy.common.interf.BaseFragmentInterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

/**
 * @author lipeiyong
 * @date 2019/3/20 16:59
 */
public abstract class BaseFragment<T extends ViewDataBinding> extends Fragment implements BaseFragmentInterface {

    /**
     * 接收传递过来的参数
     */
    protected Bundle args;

    protected T mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        args = getArguments();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);

        init();

        return mBinding.getRoot();
    }

    protected abstract int getLayoutId();

    protected void simpleDialog(String msg) {
        new AlertDialog.Builder(getActivity())
                .setTitle("提示")
                .setMessage(msg)
                .setNegativeButton("确认", null)
                .show();
    }

    private Toast toast;

    protected void toast(String msg) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    protected void toast(@StringRes int msg) {
        toast(getString(msg));
    }

    protected boolean hasPermission(String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || PackageManager.PERMISSION_GRANTED == getActivity().checkSelfPermission(permission);
    }

    protected void processWithPermission(Runnable runnable, String permission) {
        if (hasPermission(permission)) {
            process(runnable);
        } else {
            String hint = "请授予权限以继续操作。";
            processWithNoPermission(runnable, permission, hint);
        }
    }

    protected void processWithPermission(Runnable runnable, String permission, String reason) {
        if (hasPermission(permission)) {
            process(runnable);
        } else {
            processWithNoPermission(runnable, permission, reason);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void processWithNoPermission(Runnable runnable, String permission, String reason) {
        int reqCode = Math.abs(permission.hashCode()) >> 16;
        if (shouldShowRequestPermissionRationale(permission)) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("请授予权限")
                    .setMessage(reason)
                    .setPositiveButton("朕赏你们", (dialog, which) -> {
                        requestPermissions(new String[]{permission}, Math.abs(reqCode));
                        getRunnableSparseArray().append(reqCode, new RunnableReason(runnable, reason));
                    })
                    .setNegativeButton("朕便不", null)
                    .show();
        } else {
            requestPermissions(new String[]{permission}, Math.abs(reqCode));
            getRunnableSparseArray().append(reqCode, new RunnableReason(runnable, reason));
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; i++) {
            RunnableReason runnableReason = getRunnableSparseArray().get(Math.abs(permissions[i].hashCode()) >> 16);
            if (null != runnableReason) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    process(runnableReason.runnable);
                } else {
                    processWithNoPermission(runnableReason.runnable, permissions[i], runnableReason.reason);
                }
            }
        }
    }

    private Handler handler;
    private SparseArray<RunnableReason> runnableSparseArray;

    private static class RunnableReason {
        Runnable runnable;
        String reason;

        public RunnableReason(Runnable runnable, String reason) {
            this.runnable = runnable;
            this.reason = reason;
        }
    }

    private SparseArray<RunnableReason> getRunnableSparseArray() {
        if (runnableSparseArray == null) {
            runnableSparseArray = new SparseArray<>(1);
        }
        return runnableSparseArray;
    }

    protected void process(Runnable runnable) {
        getHandler().post(runnable);
    }

    protected Handler getHandler() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        return handler;
    }
}
