package com.komlin.libcommon.util.encryp;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;

/**
 * @author lipeiyong
 * @date 2019/8/16 14:45
 */
public class CustomGsonRequestBodyConverter<T> implements Converter<T, RequestBody> {

    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final Gson gson;
    private final TypeAdapter<T> adapter;

    CustomGsonRequestBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public RequestBody convert(T value) {
        byte[] encrypt = AESUtils.encryptToByte(value.toString());
        return RequestBody.create(MEDIA_TYPE, encrypt);
    }
}
