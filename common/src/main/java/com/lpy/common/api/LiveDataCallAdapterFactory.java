package com.lpy.common.api;

import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.lpy.common.util.LiveDataCallAdapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.CallAdapter;
import retrofit2.Retrofit;


/**
 * @author lipeiyong
 */
public class LiveDataCallAdapterFactory extends CallAdapter.Factory {

    @Override
    public CallAdapter<?, ?> get(@NonNull Type returnType, @NonNull Annotation[] annotations, @NonNull Retrofit retrofit) {
        if (getRawType(returnType) != LiveData.class) {
            return null;
        }
        Type observableType = getParameterUpperBound(0, (ParameterizedType) returnType);
        Class<?> rawObservableType = getRawType(observableType);
        if (rawObservableType != ApiResult.class) {
            throw new IllegalArgumentException("type must be a ApiResult");
        }
        if (!(observableType instanceof ParameterizedType)) {
            throw new IllegalArgumentException("ApiResult must be parameterized");
        }
        //Type bodyType = getParameterUpperBound(0, (ParameterizedType) observableType);
        return new LiveDataCallAdapter<>(observableType);
    }
}
