package com.lpy.common.util;

import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.lpy.common.api.ApiResult;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * A Retrofit adapter that converts the Call into a LiveData of Response.
 *
 * @author lipeiyong
 */
public class LiveDataCallAdapter<R> implements CallAdapter<ApiResult<R>, LiveData<ApiResult<R>>> {
    private final Type responseType;

    public LiveDataCallAdapter(Type responseType) {
        this.responseType = responseType;
    }

    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public LiveData<ApiResult<R>> adapt(@NonNull final Call<ApiResult<R>> call) {
        return new LiveData<ApiResult<R>>() {
            AtomicBoolean started = new AtomicBoolean(false);

            @Override
            protected void onActive() {
                super.onActive();
                if (started.compareAndSet(false, true)) {
                    call.enqueue(new Callback<ApiResult<R>>() {
                        @Override
                        public void onResponse(@NonNull Call<ApiResult<R>> call, @NonNull Response<ApiResult<R>> response) {
                            if (response.isSuccessful()) {
//                                if (BuildConfig.DEBUG) {
                                    String url = call.request().url().toString();
                                    String api = url.substring(url.lastIndexOf('/'));
                                    Timber.i("OKHttp Response, Api=[%s] , Body=[%s]", api, response.body());
//                                }

                                postValue(response.body());
                            } else {
//                                if (BuildConfig.DEBUG) {
                                    String url = call.request().url().toString();
                                    String api = url.substring(url.lastIndexOf('/'));
                                    Timber.w("OKHttp Failure, Api=[%s] , Code=[%d] , Msg=[%s]", api, response.code(), response.message());
//                                }
                                ApiResult<R> apiResult = new ApiResult<>();
                                apiResult.code = response.code();
                                apiResult.msg = response.message();
                                apiResult.data = null;
                                postValue(apiResult);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ApiResult<R>> call, @NonNull Throwable t) {
//                            if (BuildConfig.DEBUG) {
                                String url = call.request().url().toString();
                                String api = url.substring(url.lastIndexOf('/'));
                                Timber.w("OKHttp Error, Api=[%s] , Error=[%s]", api, t);
//                            }
                            ApiResult<R> apiResult = new ApiResult<>();
                            apiResult.code = 999;
                            apiResult.msg = t.getMessage();
                            apiResult.data = null;
                            postValue(apiResult);
                        }
                    });
                }
            }
        };
    }
}
