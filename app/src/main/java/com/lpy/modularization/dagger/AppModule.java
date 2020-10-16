package com.lpy.modularization.dagger;

import android.app.Application;

import com.komlin.libcommon.api.LiveDataCallAdapterFactory;
import com.komlin.libcommon.util.AppExecutors;
import com.lpy.modularization.CustomApplication;
import com.lpy.modularization.api.ApiService;
import com.lpy.modularization.api.Constants;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

/**
 * 基础依赖提供
 *
 * @author lipeiyong
 */
@Module()
public class AppModule {

    @Singleton
    @Provides
    Application provideApplication(CustomApplication app) {
        return app;
    }

    @Singleton
    @Provides
    AppExecutors appExecutors() {
        return new AppExecutors();
    }

    @Singleton
    @Provides
    OkHttpClient provideOkHttp() {
        return new OkHttpClient.Builder()
                .connectTimeout(8000, TimeUnit.MILLISECONDS)
                .readTimeout(8000, TimeUnit.MILLISECONDS)
                .writeTimeout(8000, TimeUnit.MILLISECONDS)
                .addNetworkInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder newBuilder = original.newBuilder();
//                    newBuilder.addHeader("deviceCode", sn);
                    Timber.v("OKHttp Request URL= [%s]", original.url());
                    if (Objects.equals("POST", original.method())) {
                        RequestBody bodyUnSign = original.body();
                        assert bodyUnSign != null;
                        /*todo AES*/
                        newBuilder.post(bodyUnSign);
                    } else if (Objects.equals("DELETE", original.method())) {
                        newBuilder.delete();
                    } else if (Objects.equals("PUT", original.method())) {
                        RequestBody bodyUnSign = original.body();
                        assert bodyUnSign != null;
                        newBuilder.put(bodyUnSign);
                    } else {
                        newBuilder.get();
                    }
                    Request request = newBuilder.build();
                    return chain.proceed(request);
                })

                .build();
    }

    @Singleton
    @Provides
    ApiService provideApiService(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .baseUrl(Constants.BASE_URL)
                .client(okHttpClient)
                .build()
                .create(ApiService.class);
    }

}
