package com.lpy.modulec;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.lpy.comm.RouteUtils;
import com.lpy.common.base.standard.BaseActivity;
import com.lpy.modulec.databinding.ModcActivityMainBinding;

/**
 * @author lipeiyong
 * @date 2020/9/30 10:31 AM
 */
@Route(path = RouteUtils.ModC_Activity_Main)
public class MainActivity extends BaseActivity<ModcActivityMainBinding> {
    @Override
    protected int getLayoutId() {
        return R.layout.modc_activity_main;
    }

    @Override
    public void init(Bundle savedInstanceState) {

    }
}
