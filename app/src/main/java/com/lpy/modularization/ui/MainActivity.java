package com.lpy.modularization.ui;

import android.os.Bundle;

import com.alibaba.android.arouter.launcher.ARouter;
import com.komlin.libcommon.base.BaseActivity;
import com.komlin.libcommon.arouter.RouteUtils;
import com.lpy.modularization.R;
import com.lpy.modularization.databinding.ActivityMainBinding;

/**
 * @author lipeiyong
 */
public class MainActivity extends BaseActivity<ActivityMainBinding> {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mBinding.btn.setOnClickListener(v -> {
            ARouter.getInstance().build(RouteUtils.ModA_Activity_Main).navigation();
        });
    }
}
