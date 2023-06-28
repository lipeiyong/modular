package com.lpy.common.util.encryp;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * @author lipeiyong
 * @date 2019/8/16 14:30
 */
public class CustomGsonConverterFactory extends Converter.Factory {

    public static CustomGsonConverterFactory create() {
        return create(new Gson());
    }

    @SuppressWarnings("ConstantConditions") // Guarding public API nullability.
    public static CustomGsonConverterFactory create(Gson gson) {
        if (gson == null) {
            throw new NullPointerException("gson == null");
        }
        return new CustomGsonConverterFactory(gson);
    }

    private final Gson gson;

    private CustomGsonConverterFactory(Gson gson) {
        this.gson = gson;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new CustomGsonResponseBodyConverter<>(gson, adapter);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new CustomGsonRequestBodyConverter<>(gson, adapter);
    }
}
