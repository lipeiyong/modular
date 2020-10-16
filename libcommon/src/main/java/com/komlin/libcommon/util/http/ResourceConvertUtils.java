package com.komlin.libcommon.util.http;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.MainThread;

import com.komlin.libcommon.api.ApiResult;
import com.komlin.libcommon.api.Resource;

/**
 * @author lipeiyong
 * @date on 2018/7/16 上午10:49
 */
public class ResourceConvertUtils {
    @Deprecated
    @MainThread
    public static <T> LiveData<Resource<ApiResult<T>>> convert(LiveData<ApiResult<T>> source) {
        MutableLiveData<Resource<ApiResult<T>>> map = LiveDataTransformations.map(source, Resource::success);
        map.setValue(Resource.loading(null));
        return map;
    }

    @MainThread
    public static <T,D extends ApiResult<T>> LiveData<Resource<T>> convertToResource(LiveData<D> source) {
        MutableLiveData<Resource<T>> map = LiveDataTransformations.map(source, input -> {
            if (null == input) {
                return Resource.error(0x8000, "UnKnow Error !!!", null);
            } else if (input.isSuccessful()) {
                return Resource.success(input.data);
            } else {
                return Resource.error(input.code, input.errorMsg(), input.data);
            }
        });
        map.setValue(Resource.loading(null));
        return map;
    }
}
