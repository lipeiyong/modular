package com.komlin.libcommon.api;

import android.content.SharedPreferences;


import com.komlin.libcommon.util.Constants;
import com.komlin.libcommon.util.SP_Utils;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author lipeiyong
 */
@Singleton
public class TokenManager {

    private String tokenLiveData;

    @Inject
    public TokenManager() {
        tokenLiveData = SP_Utils.getString(Constants.SP_USER_TOKEN, null);
        SP_Utils.instance().registerOnSharedPreferenceChangeListener(listener);
    }

    private final SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            if (Constants.SP_USER_TOKEN.equals(s)) {
                tokenLiveData = sharedPreferences.getString(s, null);
            }
        }
    };


    public String getToken() {
        return tokenLiveData;
    }
}
