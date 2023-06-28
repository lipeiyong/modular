package com.lpy.common.base;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import android.view.MenuItem;
import android.widget.Toast;

import com.lpy.common.interf.BaseViewInterface;
import com.lpy.common.util.ActivityManager;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;


/**
 * @author lipeiyong
 * @date 2019/3/20 16:59
 */
public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity implements BaseViewInterface {

    public static final String DEFAULT_PERMISSION_HINT = "缺少此权限应用将无法执行后续操作。";
    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000; //需要自己定义标志
    protected T mBinding;
    private Toast toast;
    private Handler handler;
    private SparseArray<PermissionRunnable> runnableSparseArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);//关键代码
        ActivityManager.getInstance().addActivity(this);

        mBinding = DataBindingUtil.setContentView(this, getLayoutId());
        init(savedInstanceState);
    }

    /**
     * 获取布局
     *
     * @return
     */
    protected abstract int getLayoutId();

    @Override
    public void onDestroy() {
        super.onDestroy();
        ActivityManager.getInstance().removeActivity(this);
        if (null != handler) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    protected void simpleDialog(String msg) {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage(msg)
                .setNegativeButton("确认", null)
                .show();
    }

    protected final void toast(String msg) {
        if (toast != null) {
            toast.cancel();
            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    protected final void toast(@StringRes int msg) {
        toast(getString(msg));
    }

    protected final boolean hasPermission(String[] permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        for (String permission : permissions) {
            if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(permission)) {
                return false;
            }
        }
        return true;
    }

    protected final void processWithPermission(@Nullable Runnable grantedRunnable, @NonNull String[] permissions) {
        processWithPermission(grantedRunnable, null, permissions, DEFAULT_PERMISSION_HINT);
    }

    protected final void processWithPermission(@Nullable Runnable grantedRunnable, @NonNull String[] permissions, String reson) {
        processWithPermission(grantedRunnable, null, permissions, reson);
    }

    protected final void processWithPermission(@Nullable Runnable grantedRunnable, @Nullable Runnable deniedRunnable, @NonNull String[] permissions) {
        processWithPermission(grantedRunnable, deniedRunnable, permissions, DEFAULT_PERMISSION_HINT);
    }

    protected final void processWithPermission(@Nullable Runnable grantedRunnable, @Nullable Runnable deniedRunnable, @NonNull String[] permissions, @Nullable String reason) {
        processWithPermission(new PermissionRunnable(grantedRunnable, deniedRunnable, permissions, reason));
    }

    protected final void processWithPermission(final PermissionRunnable runnable) {
        if (hasPermission(runnable.permissions)) {
            execute(runnable.grantedRunnable);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                for (String permission : runnable.permissions) {
                    if (shouldShowRequestPermissionRationale(permission)) {
                        new AlertDialog.Builder(this)
                                .setTitle("请授予权限")
                                .setMessage(runnable.reason)
                                .setPositiveButton("朕赏你们", (dialog, which) -> doRequest(runnable))
                                .setNegativeButton("朕偏不", (dialog, which) -> execute(runnable.deniedRunnable))
                                .show();
                        return;
                    }
                }
                doRequest(runnable);
            }
        }
    }

    private String getChineseName(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
            case Manifest.permission.ACCESS_COARSE_LOCATION:
                return "定位";
            case Manifest.permission.READ_EXTERNAL_STORAGE:
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                return "存储";
            case Manifest.permission.CALL_PHONE:
                return "电话";
            case Manifest.permission.CAMERA:
                return "相机";
            default:
                return "";
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void doRequest(PermissionRunnable runnable) {
        int requestCode = Math.abs(Arrays.hashCode(runnable.permissions)) >> 16;
        requestPermissions(runnable.permissions, requestCode);
        getRunnableSparseArray().append(requestCode, runnable);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionRunnable permissionRunnable = getRunnableSparseArray().get(Math.abs(Arrays.hashCode(permissions)) >> 16);
        if (null != permissionRunnable) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
//                    processWithPermission(permissionRunnable);
                    return;
                }
            }
            execute(permissionRunnable.grantedRunnable);
        }
    }

    private SparseArray<PermissionRunnable> getRunnableSparseArray() {
        if (runnableSparseArray == null) {
            runnableSparseArray = new SparseArray<>(1);
        }
        return runnableSparseArray;
    }

    protected final void execute(Runnable runnable) {
        if (runnable != null) {
            getHandler().post(runnable);
        }
    }

    protected final Handler getHandler() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        return handler;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class PermissionRunnable {
        final Runnable grantedRunnable;
        final Runnable deniedRunnable;
        final String[] permissions;
        final String reason;

        PermissionRunnable(Runnable runnable, Runnable deniedRunnable, String[] permissions, String reason) {
            this.deniedRunnable = deniedRunnable;
            this.grantedRunnable = runnable;
            this.permissions = permissions;
            this.reason = reason;
        }
    }
}
