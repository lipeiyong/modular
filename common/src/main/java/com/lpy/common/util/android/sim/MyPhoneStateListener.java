package com.lpy.common.util.android.sim;

import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;

/**
 * 手机状态监听
 *
 * @author lipeiyong
 * @date 2019/11/13
 */
public class MyPhoneStateListener extends PhoneStateListener {

    private OnSignalStrengthsChangedListener onSignalStrengthsChangedListener;

    public void setOnSignalStrengthsChangedListener(OnSignalStrengthsChangedListener onSignalStrengthsChangedListener) {
        this.onSignalStrengthsChangedListener = onSignalStrengthsChangedListener;
    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        if (onSignalStrengthsChangedListener != null) {
            onSignalStrengthsChangedListener.onSignalStrengthsChanged(signalStrength);
        }
    }

    public interface OnSignalStrengthsChangedListener {
        void onSignalStrengthsChanged(SignalStrength signalStrength);
    }
}
