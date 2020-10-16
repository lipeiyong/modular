package com.komlin.libcommon.util.http;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author lipeiyong
 * @date on 2018/7/26 下午4:42
 */
public class LiveDataTransformations {
    @MainThread
    public static <X, Y> MediatorLiveData<Y> map(@NonNull LiveData<X> source,
                                                 @NonNull final Function<X, Y> func) {
        final MediatorLiveData<Y> result = new MediatorLiveData<>();
        result.addSource(source, x -> result.setValue(func.apply(x)));
        return result;
    }

    @MainThread
    public static <X, Y> MediatorLiveData<Y> switchMap(@NonNull LiveData<X> trigger,
                                                       @NonNull final Function<X, LiveData<Y>> func) {
        final MediatorLiveData<Y> result = new MediatorLiveData<>();
        result.addSource(trigger, new Observer<X>() {
            LiveData<Y> mSource;

            @Override
            public void onChanged(@Nullable X x) {
                LiveData<Y> newLiveData = func.apply(x);
                if (mSource == newLiveData) {
                    return;
                }
                if (mSource != null) {
                    result.removeSource(mSource);
                }
                mSource = newLiveData;
                if (mSource != null) {
                    result.addSource(mSource, y -> result.setValue(y));
                }
            }
        });
        return result;
    }
}
