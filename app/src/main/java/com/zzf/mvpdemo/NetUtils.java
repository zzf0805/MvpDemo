package com.zzf.mvpdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class NetUtils {

    private static final int DEFAULT_TIMEOUT = 10;
    private static final String HOST = "http://v.juhe.cn/toutiao/";
    private static DemoApi demoApi;
    private static NetUtils netUtils;

    private NetUtils() {
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
        okBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        okBuilder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        okBuilder.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        okBuilder.addInterceptor(new LoggerInterceptor());

        //https设置
        okBuilder.sslSocketFactory(createSSLSocketFactory());
        okBuilder.hostnameVerifier(new TrustAllHostnameVerifier());
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okBuilder.build())
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(HOST)
                .build();

        demoApi = retrofit.create(DemoApi.class);
    }


    public DemoApi getDemoApi(){
        return demoApi;
    }

    public static NetUtils getInstance(){
        if(netUtils == null){
            netUtils = new NetUtils();
        }
        return netUtils;
    }

    /**
     * 针对返回数据为空进行数据解析而改造的class
     */
    public static class NullOnEmptyConverterFactory extends Converter.Factory {

        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            final Converter<ResponseBody, ?> delegate = retrofit.nextResponseBodyConverter(this, type, annotations);
            return new Converter<ResponseBody, Object>() {
                @Override
                public Object convert(@NonNull ResponseBody body) throws IOException {
                    if (body.contentLength() == 0) return null;
                    return delegate.convert(body);
                }
            };
        }
    }


    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @SuppressLint("BadHostnameVerifier")
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ssfFactory;
    }


    @SuppressLint("TrustAllX509TrustManager")
    private static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }


    /**
     * 打印日志拦截器
     */
    public class LoggerInterceptor implements Interceptor {

        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            //获得请求信息，此处如有需要可以添加headers信息
            Request request = chain.request();
            //记录请求耗时
            long startNs = System.nanoTime();
            okhttp3.Response response;
            try {
                //发送请求，获得相应，
                response = chain.proceed(request);
                long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
                //打印请求耗时
                //使用response获得headers(),可以更新本地Cookie。
                Headers headers = response.headers();
                //获得返回的body，注意此处不要使用responseBody.string()获取返回数据，原因在于这个方法会消耗返回结果的数据(buffer)
                ResponseBody responseBody = response.body();
                //为了不消耗buffer，我们这里使用source先获得buffer对象，然后clone()后使用
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE); // Buffer the entire body.
                //获得返回的数据
                Buffer buffer = source.buffer();
                //使用前clone()下，避免直接消耗
                Log.i("demohttp", "url: " + request.url()
                        //打印请求信息
                        + "\nheaders: " + headers.toString()
                        + "\nmethod: " + request.method()
                        + "\nrequest-body: null"
                        + "\n耗时:" + tookMs + "ms"
                        + "\nresponse:" + buffer.clone().readString(Charset.forName("UTF-8")));
            } catch (Exception e) {
                throw e;
            }
            return response;
        }
    }
}
