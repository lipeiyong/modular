package com.lpy.modularization.dagger;

import androidx.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;

/**
 * @author lipeiyong
 */
@Module
public abstract class ViewModelModule {
    @Binds
    abstract ViewModelProvider.Factory provideViewModelFactory(ViewModelFactory viewModelFactory);

//    @Binds
//    @IntoMap
//    @ViewModelKey(HealthViewModel.class)
//    abstract ViewModel healthViewModel(HealthViewModel viewModel);
}
