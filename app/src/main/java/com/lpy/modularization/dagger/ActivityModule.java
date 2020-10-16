package com.lpy.modularization.dagger;


import com.komlin.libcommon.dagger.scope.ActivityScope;
import com.lpy.modularization.ui.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * @author lipeiyong
 */
@Module
public interface ActivityModule {

    @ActivityScope
    @ContributesAndroidInjector
    MainActivity contributeMainActivity();

}
