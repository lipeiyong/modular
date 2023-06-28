package com.lpy.moduleb;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.lpy.comm.IModuleCService;
import com.lpy.comm.RouteUtils;
import com.lpy.common.base.standard.BaseActivity;
import com.lpy.moduleb.databinding.ModbActivityMainBinding;

import java.util.Locale;

import timber.log.Timber;

/**
 * @author lipeiyong
 * @date 2020/9/30 10:13 AM
 */
@Route(path = RouteUtils.ModB_Activity_Main)
public class MainActivity extends BaseActivity<ModbActivityMainBinding> {
    @Override
    protected int getLayoutId() {
        return R.layout.modb_activity_main;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        String value = getIntent().getStringExtra("value");
        mBinding.tv.setText(String.format(Locale.getDefault(), "接收到的数据是[%s]", value));

        mBinding.btn.setOnClickListener(v -> {
            IModuleCService navigation = ARouter.getInstance().navigation(IModuleCService.class);
            if (navigation != null) {
                Timber.i(navigation.getData());
                toast(navigation.getData());
            }
        });
    }
}
