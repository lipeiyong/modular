package com.lpy.modulec;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.komlin.libcommon.arouter.RouteUtils;
import com.komlin.libcommon.arouter.communication.IModuleCService;

/**
 * @author lipeiyong
 * @date 2020/10/16 10:37 AM
 */
@Route(path = RouteUtils.ModC_Service)
public class ModulecServiceImpl implements IModuleCService {
    @Override
    public String getData() {
        return "ModuleC 返回的数据";
    }

    @Override
    public void init(Context context) {

    }
}
