package com.lpy.modular.ui;

import android.os.Bundle;

import com.alibaba.android.arouter.launcher.ARouter;
import com.lpy.comm.RouteUtils;
import com.lpy.common.base.BaseActivity;
import com.lpy.modular.R;
import com.lpy.modular.databinding.ActivityMainBinding;


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
