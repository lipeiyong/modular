package com.komlin.libcommon.base.adapter.select;

import android.widget.Checkable;

/**
 * @author lipeiyong
 * @date 2019/9/6 11:10
 */
public class CustomCheckable implements Checkable {

    private boolean mChecked;

    @Override
    public void setChecked(boolean checked) {
        mChecked = checked;
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }
}
