package com.komlin.libcommon.util.encryp;

import androidx.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * @author lipeiyong
 * @date 2019/8/16 13:55
 */
public class JsonConverterFactory extends Converter.Factory {
    private static final String TAG = "JsonConverterFactory";
    private final Gson gson;

    public static JsonConverterFactory create() {
        return create(new Gson());
    }

    public static JsonConverterFactory create(Gson gson) {
        return new JsonConverterFactory(gson);

    }

    private JsonConverterFactory(Gson gson) {
        if (gson == null) {
            throw new NullPointerException("gson == null");
        }
        this.gson = gson;
    }


    @Nullable
    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new JsonRequestBodyConverter<>(gson, adapter); //请求
    }

    @Nullable
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new JsonResponseBodyConverter<>(gson, adapter); //响应
    }

    /**
     * JsonRequestBodyConverter<T>
     *
     * @param <T>
     */
    public static class JsonRequestBodyConverter<T> implements Converter<T, RequestBody> {
        private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
        private final Gson gson;
        private final TypeAdapter<T> adapter;

        /**
         * 构造器
         */
        public JsonRequestBodyConverter(Gson gson, TypeAdapter<T> adapter) {
            this.gson = gson;
            this.adapter = adapter;
        }

        @Override
        public RequestBody convert(T value) throws IOException {

            //这里需要，特别注意的是，request是将T转换成json数据。
            //你要在T转换成json之后再做加密。
            //再将数据post给服务器，同时要注意，你的T到底指的那个对象

            //加密操作，返回字节数组
            byte[] encrypt = AESUtils.encryptToByte(value.toString());

            Log.i("xiaozhang", "request中传递的json数据：" + value.toString()); //打印：加密前的json字符串
            Log.i("xiaozhang", "加密后的字节数组：" + encrypt.toString());//打印：字节数组

            //传入字节数组，创建RequestBody 对象
            return RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), encrypt);
        }
    }

    /**
     * JsonResponseBodyConverter<T>
     *
     * @param <T>
     */
    public class JsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
        private final Gson mGson;//gson对象
        private final TypeAdapter<T> adapter;

        /**
         * 构造器
         */
        public JsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
            this.mGson = gson;
            this.adapter = adapter;
        }

        /**
         * 转换
         *
         * @param responseBody
         * @return
         * @throws IOException
         */
        @Override
        public T convert(ResponseBody responseBody) throws IOException {

            byte[] bytes = responseBody.bytes();

            //对字节数组进行解密操作
            String decryptString = AESUtils.decrypt(bytes);

            //对解密的字符串进行处理
            int position = decryptString.lastIndexOf("}");
            String jsonString = decryptString.substring(0, position + 1);

            Log.i(TAG, "需要解密的服务器数据字节数组：" + bytes.toString());
            Log.i(TAG, "解密后的服务器数据字符串：" + decryptString);
            Log.i(TAG, "解密后的服务器数据字符串处理为json：" + jsonString);

            //这部分代码参考GsonConverterFactory中GsonResponseBodyConverter<T>的源码对json的处理
            Reader reader = StringToReader(jsonString);
            JsonReader jsonReader = gson.newJsonReader(reader);
            try {
                return adapter.read(jsonReader);
            } finally {
                reader.close();
                jsonReader.close();
            }
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
}
