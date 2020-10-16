package com.lpy.modularization.dagger;



import com.lpy.modularization.CustomApplication;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * @author lipeiyong
 */
@Singleton
@Component(modules = {
        AppModule.class,
        AndroidInjectionModule.class,
        AndroidSupportInjectionModule.class,
        ActivityModule.class,
        ViewModelModule.class,
        FragmentModule.class})
public interface AppComponent extends AndroidInjector<CustomApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(CustomApplication application);

        AppComponent build();
    }

}
