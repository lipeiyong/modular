package com.lpy.common.api;

import android.content.SharedPreferences;


import com.lpy.common.util.Constants;
import com.lpy.common.util.SP_Utils;


/**
 * @author lipeiyong
 */
//@Singleton
public class TokenManager {

    private String tokenLiveData;

//    @Inject
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
