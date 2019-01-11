package com.jeff.dominatelight.http;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by MYFLY on 2016/1/6.
 */
public class HttpHelper {

    private static HttpHelper mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mDelivery;
    private Gson mGson;


    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private HttpHelper() {
        mOkHttpClient = new OkHttpClient();
        //cookie enabled
        mOkHttpClient.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
        mOkHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
        mOkHttpClient.setWriteTimeout(10, TimeUnit.SECONDS);
        mOkHttpClient.setReadTimeout(10, TimeUnit.SECONDS);
        mDelivery = new Handler(Looper.getMainLooper());
        mGson = new Gson();
    }

    public static HttpHelper getInstance() {
        if (mInstance == null) {
            synchronized (HttpHelper.class) {
                if (mInstance == null) {
                    mInstance = new HttpHelper();
                }
            }
        }
        return mInstance;
    }


    /**
     * 同步的Get请求
     *
     * @param url
     * @return Response
     */
    private Response _getAsyn(String url) throws IOException {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        Response execute = call.execute();
        return execute;
    }

    /**
     * 同步的Get请求
     *
     * @param url
     * @return 字符串
     */
    private String _getAsString(String url) throws IOException {
        Response execute = _getAsyn(url);
        return execute.body().string();
    }


    /**
     * 异步的get请求
     *
     * @param url
     * @param callback
     */
    private void _getAsyn(String url, final ResultCallback callback) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        deliveryResult(callback, request);
    }

    /**
     * 异步的get请求
     *
     * @param url
     * @param callback
     */
    private void _getAsyn(String url, Map<String, String> header, final ResultCallback callback) {
        Headers headers = Headers.of(header);
        final Request request = new Request.Builder()
                .headers(headers)
                .url(url)
                .build();
        deliveryResult(callback, request);
    }


    /**
     * 同步的Post请求
     *
     * @param url
     * @param params post的参数
     * @return
     */
    private Response _post(String url, Map<String, String> params) throws IOException {
        Request request = buildPostRequest(url, params);
        Response response = mOkHttpClient.newCall(request).execute();
        return response;
    }


    /**
     * 同步的Post请求
     *
     * @param url
     * @param params post的参数
     * @return 字符串
     */
    private String _postAsString(String url, Map<String, String> params) throws IOException {
        Response response = _post(url, params);
        return response.body().string();
    }

    /**
     * 异步的post请求
     *
     * @param url
     * @param callback
     * @param params
     */
    private void _postAsyn(String url, final ResultCallback callback, Map<String, String> params) {
        Request request = buildPostRequest(url, params);
        deliveryResult(callback, request);
    }

    private void _postAsyn(String url, final ResultCallback callback, Map<String, String> head, Map<String, Object> params) {
        Request request = buildPostRequest(url, head, params);
        deliveryResult(callback, request);
    }

    private void _putAsyn(String url, Map<String, String> header, Map<String, String> params, final ResultCallback callback) {
        Request request = buildPutRequest(url, header, params);
        deliveryResult(callback, request);
    }

    private void _deleteAsyn(String url, Map<String, String> header, Map<String, String> params, final ResultCallback callback) {
        Request request = buildDeleteRequest(url, header, params);
        deliveryResult(callback, request);
    }

    //*************对外公布的方法************


    public static Response getAsyn(String url) throws IOException {
        return getInstance()._getAsyn(url);
    }


    public static String getAsString(String url) throws IOException {
        return getInstance()._getAsString(url);
    }

    public static void getAsyn(String url, ResultCallback callback) {
        getInstance()._getAsyn(url, callback);
    }

    public static void getAsyn(String url, Map<String, String> header, ResultCallback callback) {
        getInstance()._getAsyn(url, header, callback);
    }

    public static Response post(String url, Map<String, String> params) throws IOException {
        return getInstance()._post(url, params);
    }

    public static String postAsString(String url, Map<String, String> params) throws IOException {
        return getInstance()._postAsString(url, params);
    }

    public static void postAsyn(String url, Map<String, String> params, final ResultCallback callback) {
        getInstance()._postAsyn(url, callback, params);
    }

    public static void postAsyn(String url, Map<String, String> head, Map<String, Object> params, final ResultCallback callback) {
        getInstance()._postAsyn(url, callback, head, params);
    }

    public static void putAsyn(String url, Map<String, String> header, Map<String, String> params, final ResultCallback callback) {
        getInstance()._putAsyn(url, header, params, callback);
    }

    public static void deleteAsyn(String url, Map<String, String> header, Map<String, String> params, final ResultCallback callback) {
        getInstance()._deleteAsyn(url, header, params, callback);
    }

    //****************************

    private void deliveryResult(final ResultCallback callback, Request request) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                sendFailedStringCallback(request, e, callback);
            }

            @Override
            public void onResponse(final Response response) {
                try {
                    final int code = response.code();
                    final String string = response.body().string();
                    if (callback.mType == String.class) {
                        sendSuccessResultCallback(code, string, callback);
                    } else {
                        Object o = mGson.fromJson(string, callback.mType);
                        sendSuccessResultCallback(code, o, callback);
                    }


                } catch (IOException e) {
                    sendFailedStringCallback(response.request(), e, callback);
                } catch (com.google.gson.JsonParseException e)//Json解析的错误
                {
                    sendFailedStringCallback(response.request(), e, callback);
                }

            }
        });
    }

    private void sendFailedStringCallback(final Request request, final Exception e, final ResultCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null)
                    callback.onError(request, e);
            }
        });
    }

    private void sendSuccessResultCallback(final int code, final Object object, final ResultCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onResponse(code, object);
                }
            }
        });
    }

    private Request buildPostRequest(String url, Map<String, String> params) {
        if (params == null) {
            params = new HashMap<String, String>();
        }
        RequestBody body = RequestBody.create(JSON, new Gson().toJson(params));
        return new Request.Builder()
                .url(url)
                .post(body)
                .build();
    }

    private Request buildPostRequest(String url, Map<String, String> header, Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<String, Object>();
        }
        Headers headers = Headers.of(header);
        RequestBody body = RequestBody.create(JSON, new Gson().toJson(params));
        return new Request.Builder()
                .headers(headers)
                .url(url)
                .post(body)
                .build();
    }

    private Request buildPutRequest(String url, Map<String, String> header, Map<String, String> params) {
        if (params == null) {
            params = new HashMap<String, String>();
        }
        Headers headers = Headers.of(header);
        RequestBody body = RequestBody.create(JSON, new Gson().toJson(params));
        return new Request.Builder()
                .headers(headers)
                .url(url)
                .put(body)
                .build();
    }

    private Request buildDeleteRequest(String url, Map<String, String> header, Map<String, String> params) {
        if (params == null) {
            params = new HashMap<String, String>();
        }
        Headers headers = Headers.of(header);
        RequestBody body = RequestBody.create(JSON, new Gson().toJson(params));
        return new Request.Builder()
                .headers(headers)
                .url(url)
                .delete(body)
                .build();
    }

    public static abstract class ResultCallback<T> {
        Type mType;

        public ResultCallback() {
            mType = getSuperclassTypeParameter(getClass());
        }

        static Type getSuperclassTypeParameter(Class<?> subclass) {
            Type superclass = subclass.getGenericSuperclass();
            System.out.println(superclass);
            if (superclass instanceof Class) {
                System.out.println(superclass);
            }
            ParameterizedType parameterized = (ParameterizedType) superclass;
            return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
        }

        public abstract void onError(Request request, Exception e);

        public abstract void onResponse(int code, T response);
    }

}
