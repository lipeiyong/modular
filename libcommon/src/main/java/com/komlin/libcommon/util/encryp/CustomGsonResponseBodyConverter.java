package com.komlin.libcommon.util.encryp;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import timber.log.Timber;

/**
 * @author lipeiyong
 * @date 2019/8/16 14:40
 */
public class CustomGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private static final String TAG = "CustomGsonResponseBodyC";

    private final Gson gson;
    private final TypeAdapter<T> adapter;

    CustomGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        byte[] bytes = value.bytes();
        String jsonString = AESUtils.decrypt(new String(bytes));
        Timber.i(jsonString);
        Reader reader = StringToReader(jsonString);
        JsonReader jsonReader = gson.newJsonReader(reader);
        try {
            return adapter.read(jsonReader);
        } finally {
            reader.close();
            jsonReader.close();
        }

//        JsonReader jsonReader = gson.newJsonReader(value.charStream());
//        try {
//            return adapter.read(jsonReader);
//        } finally {
//            value.close();
//        }
    }

    /**
     * String转Reader
     *
     * @param json
     * @return
     */
    private Reader StringToReader(String json) {
        Reader reader = new StringReader(json);
        return reader;
    }
}
