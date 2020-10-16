package com.komlin.libcommon.base.standard;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.komlin.libcommon.interf.BaseViewInterface;
import com.komlin.libcommon.util.ActivityManager;


/**
 * @author lipeiyong
 * @date 2019/3/20 16:59
 */
public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity implements BaseViewInterface {

    public static final String DEFAULT_PERMISSION_HINT = "缺少此权限应用将无法执行后续操作。";
    protected T mBinding;
    private Toast toast;
    private Handler handler;
    private SparseArray<PermissionRunnable> runnableSparseArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    protected final boolean hasPermission(String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || PackageManager.PERMISSION_GRANTED == checkSelfPermission(permission);
    }

    protected final void processWithPermission(@Nullable Runnable grantedRunnable, @NonNull String permission) {
        processWithPermission(grantedRunnable, null, permission, DEFAULT_PERMISSION_HINT);
    }

    protected final void processWithPermission(@Nullable Runnable grantedRunnable, @NonNull String permission, String reson) {
        processWithPermission(grantedRunnable, null, permission, reson);
    }

    protected final void processWithPermission(@Nullable Runnable grantedRunnable, @Nullable Runnable deniedRunnable, @NonNull String permission) {
        processWithPermission(grantedRunnable, deniedRunnable, permission, DEFAULT_PERMISSION_HINT);
    }

    protected final void processWithPermission(@Nullable Runnable grantedRunnable, @Nullable Runnable deniedRunnable, @NonNull String permission, @Nullable String reason) {
        processWithPermission(new PermissionRunnable(grantedRunnable, deniedRunnable, permission, reason));
    }

    protected final void processWithPermission(final PermissionRunnable runnable) {
        if (hasPermission(runnable.permission)) {
            execute(runnable.grantedRunnable);
        } else {
            if (shouldShowRequestPermissionRationale(runnable.permission)) {
                new AlertDialog.Builder(this)
                        .setTitle("请授予" + getChineseName(runnable.permission) + "权限")
                        .setMessage(runnable.reason)
                        .setPositiveButton("朕赏你们", (dialog, which) -> doRequest(runnable))
                        .setNegativeButton("朕便不", (dialog, which) -> execute(runnable.deniedRunnable))
                        .show();
            } else {
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
        int requestCode = Math.abs(runnable.permission.hashCode()) >> 16;
        requestPermissions(new String[]{runnable.permission}, requestCode);
        getRunnableSparseArray().append(requestCode, runnable);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; i++) {
            PermissionRunnable permissionRunnable = getRunnableSparseArray().get(Math.abs(permissions[i].hashCode()) >> 16);
            if (null != permissionRunnable) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    execute(permissionRunnable.grantedRunnable);
                } else {
                    processWithPermission(permissionRunnable);
                }
            }
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
        final String permission;
        final String reason;

        PermissionRunnable(Runnable runnable, Runnable deniedRunnable, String permission, String reason) {
            this.deniedRunnable = deniedRunnable;
            this.grantedRunnable = runnable;
            this.permission = permission;
            this.reason = reason;
        }
    }
}
