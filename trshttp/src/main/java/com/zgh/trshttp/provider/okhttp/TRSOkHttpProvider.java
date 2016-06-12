package com.zgh.trshttp.provider.okhttp;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;


import com.squareup.okhttp.Cache;
import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.zgh.trshttp.TRSHttpUtil;
import com.zgh.trshttp.callback.TRSBaseHttpCallback;
import com.zgh.trshttp.callback.TRSFileDownloadHttpCallback;
import com.zgh.trshttp.callback.TRSFileUploadHttpCallback;
import com.zgh.trshttp.callback.TRSStringHttpCallback;
import com.zgh.trshttp.provider.TRSBaseHttpProvider;
import com.zgh.trshttp.provider.okhttp.body.ProgressRequestBody;
import com.zgh.trshttp.provider.okhttp.body.ProgressResponseBody;
import com.zgh.trshttp.request.TRSHttpRequest;
import com.zgh.trshttp.util.ConfigUtil;
import com.zgh.trshttp.util.Constant;
import com.zgh.trshttp.util.TRSFileTypeUtil;
import com.zgh.trshttp.util.TRSFileUtil;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Vincent Woo
 * Date: 2015/12/21
 * Time: 10:30
 */
public class TRSOkHttpProvider extends TRSBaseHttpProvider {
    private OkHttpClient mOkHttpClient;
    private Handler mDelivery;
    private HashMap<String, OkHttpClient> mDownloadMap;

    public TRSOkHttpProvider() {
        mOkHttpClient = new OkHttpClient();
        mDelivery = new Handler(Looper.getMainLooper());

        File cacheDir = ConfigUtil.getCacheDir();
        int cacheSize = 20 * 1024 * 1024; // 20 MiB
        mOkHttpClient.setCache(new Cache(cacheDir.getAbsoluteFile(), cacheSize));

//        mOkHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
//        mOkHttpClient.setWriteTimeout(10, TimeUnit.SECONDS);
//        mOkHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
    }

    @Override
    public void loadString(TRSHttpRequest trsHttpRequest, final TRSStringHttpCallback callback) {
        int method = trsHttpRequest.getMethod();
        final boolean isNeedCache = trsHttpRequest.getIsNeedCache();
        HashMap<String, String> headers = trsHttpRequest.getHeaders();
        HashMap<String, String> params = trsHttpRequest.getParams();
        String url = trsHttpRequest.getUrl();
        String tag = trsHttpRequest.getTag();

        if (!url.startsWith(Constant.HTTP_PREFIX) && !url.startsWith(Constant.HTTPS_PREFIX)) {
            loadLocalString(url, callback);
            return;
        }

        final Request.Builder builder = new Request.Builder();
        builder.cacheControl(CacheControl.FORCE_NETWORK);
        if (!TextUtils.isEmpty(tag)) {
            builder.tag(tag);
        }

        //Add Http Header
        if (headers != null && headers.size() > 0) {
            Iterator iterator = headers.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = (String) entry.getKey();
                String val = (String) entry.getValue();
                builder.addHeader(key, val);
            }
        }

        //Add Post or GET Params
        if(params != null && params.size() > 0) {
            if(method == TRSHttpUtil.POST) {
                builder.post(getRequestBody(params));
            } else {
                url = getEncodedUrl(url, params);
            }
        }
        builder.url(url);

        Request request = builder.build();
        onStart(callback);

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                final String result;
                result = e.getMessage();
                if ((e instanceof SocketTimeoutException || e instanceof UnknownHostException)
                        && isNeedCache) {
                    builder.cacheControl(CacheControl.FORCE_CACHE);
                    mOkHttpClient.newCall(builder.build()).enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            String result;
                            result = response.body().string();
                            if (response.isSuccessful()) {
                                onSuccessDelivery(callback, result);
                            } else if (response.code() == 504) {
                                //No Cache yet
                                onSuccessDelivery(callback, "");
                            } else {
                                onFailDelivery(callback, result);
                            }
                        }
                    });
                } else {
                    onFailDelivery(callback, result);
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String result;
                if (response.isSuccessful()) {
                    result = response.body().string();
                    onSuccessDelivery(callback, result);
                } else {
                    result = response.code() + ": " + response.message();
                    onFailDelivery(callback, result);
                }
            }
        });
    }

    @Override
    public void uploadFile(TRSHttpRequest trsHttpRequest, final TRSFileUploadHttpCallback callback) {
        HashMap<String, String> headers = trsHttpRequest.getHeaders();
        HashMap<String, String> params = trsHttpRequest.getParams();
        HashMap<String, File> files = trsHttpRequest.getFiles();
        String url = trsHttpRequest.getUrl();
        String tag = trsHttpRequest.getTag();

        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.cacheControl(CacheControl.FORCE_NETWORK);
        if (!TextUtils.isEmpty(tag)) {
            builder.tag(tag);
        }

        //Add Http Header
        if (headers != null && headers.size() > 0) {
            Iterator iterator = headers.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = (String) entry.getKey();
                String val = (String) entry.getValue();
                builder.addHeader(key, val);
            }
        }

        //Add Post Params
        if ((params != null && params.size() > 0) || (files != null && files.size() > 0)) {
            builder.post(new ProgressRequestBody(getUploadRequestBody(params, files), callback, mDelivery));
        }

        Request request = builder.build();
        onStart(callback);

        OkHttpClient client = mOkHttpClient.clone();
        client.setConnectTimeout(30, TimeUnit.SECONDS);
        client.setWriteTimeout(30, TimeUnit.SECONDS);
        client.setReadTimeout(30, TimeUnit.SECONDS);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                final String result;
                result = e.getMessage();
                onFailDelivery(callback, result);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String result;
                if (response.isSuccessful()) {
                    result = response.body().string();
                    onUploadSuccessDelivery(callback, result);
                } else {
                    result = response.code() + ": " + response.message();
                    onFailDelivery(callback, result);
                }
            }
        });
    }

    @Override
    public void downloadFile(TRSHttpRequest trsHttpRequest, final TRSFileDownloadHttpCallback callback) {
        HashMap<String, String> headers = trsHttpRequest.getHeaders();
        HashMap<String, String> params = trsHttpRequest.getParams();
        final String url = trsHttpRequest.getUrl();
        String tag = trsHttpRequest.getTag();

        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.cacheControl(CacheControl.FORCE_NETWORK);
        if (!TextUtils.isEmpty(tag)) {
            builder.tag(tag);
        }

        //Add Http Header
        if (headers != null && headers.size() > 0) {
            Iterator iterator = headers.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = (String) entry.getKey();
                String val = (String) entry.getValue();
                builder.addHeader(key, val);
            }
        }

        //Add Post Params
        if (params != null && params.size() > 0) {
            builder.post(getRequestBody(params));
        }

        Request request = builder.build();
        onStart(callback);

        OkHttpClient client = addDownloadInterceptor(mOkHttpClient, callback);
        client.setConnectTimeout(30, TimeUnit.SECONDS);
        client.setWriteTimeout(30, TimeUnit.SECONDS);
        client.setReadTimeout(30, TimeUnit.SECONDS);

        if(!TextUtils.isEmpty(tag)) {
            mDownloadMap.put(tag, client);
        }

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                final String result;
                result = e.getMessage();
                onFailDelivery(callback, result);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String result;
                if (response.isSuccessful()) {
                    try {
                        File file = TRSFileUtil.writeFile(response.body().byteStream(),
                                Environment.getExternalStorageDirectory().getAbsolutePath()
                                        + "/Download/"
                                        + url.substring(url.lastIndexOf("/") + 1), false);
                        onDownloadSuccessDelivery(callback, file);
                    } catch (Exception e) {
                        e.printStackTrace();
                        onFailDelivery(callback, "File Write Failed!");
                    }
                } else {
                    result = response.code() + ": " + response.message();
                    onFailDelivery(callback, result);
                }
            }
        });
    }

    private OkHttpClient addDownloadInterceptor(OkHttpClient client, final TRSFileDownloadHttpCallback progressListener) {
        OkHttpClient clone = client.clone();
        //增加拦截器
        clone.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                //拦截
                Response originalResponse = chain.proceed(chain.request());
                //包装响应体并返回
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(originalResponse.body(), progressListener, mDelivery))
                        .build();
            }
        });
        return clone;
    }

    private RequestBody getUploadRequestBody(HashMap<String, String> params, HashMap<String, File> files) {
        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);

        Iterator iteratorParams = params.entrySet().iterator();
        while (iteratorParams.hasNext()) {
            Map.Entry entry = (Map.Entry) iteratorParams.next();
            String key = (String) entry.getKey();
            String val = (String) entry.getValue();
            builder.addFormDataPart(key, val);
        }

        Iterator iteratorFiles = files.entrySet().iterator();
        while (iteratorFiles.hasNext()) {
            Map.Entry entry = (Map.Entry) iteratorFiles.next();
            String key = (String) entry.getKey();
            File val = (File) entry.getValue();
            builder.addFormDataPart(key, val.getName(), RequestBody.create(MediaType.parse(getContentType(val)), val));
        }

        return builder.build();
    }

    private RequestBody getRequestBody(HashMap<String, String> params) {
        FormEncodingBuilder builder = new FormEncodingBuilder();
        Iterator iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = (String) entry.getKey();
            String val = (String) entry.getValue();
            builder.add(key, val);
        }

        return builder.build();
    }

    private String getEncodedUrl(String url, HashMap<String, String> params) {
        StringBuilder builder = new StringBuilder();
        builder.append(url).append("?");
        Iterator iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = (String) entry.getKey();
            String val = (String) entry.getValue();

            try {
                key = URLEncoder.encode(key, "UTF-8");
                val = URLEncoder.encode(val, "UTF-8");
                builder.append(key).append("=").append(val).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        String result = builder.toString();
        int len = result.length();

        return result.substring(0, len-1);
    }

    //获取文件的上传类型，图片格式为image/png,image/jpg等。非图片为application/octet-stream
    private String getContentType(File f) {
        String fileType = TRSFileTypeUtil.getFileType(f.getAbsolutePath());

        if (fileType == null || fileType.equals("")) {
            return "application/octet-stream";
        } else {
            return "image/" + fileType;
        }
    }

    private void onStart(final TRSBaseHttpCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onStart();
            }
        });
    }

    private void onFailDelivery(final TRSBaseHttpCallback callback, final String result) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onEnd();
                callback.onError(result);
            }
        });
    }

    private void onSuccessDelivery(final TRSStringHttpCallback callback, final String result) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onEnd();
                callback.onResponse(result);
            }
        });
    }

    private void onUploadSuccessDelivery(final TRSFileUploadHttpCallback callback, final String result) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onEnd();
                callback.onResponse(result);
            }
        });
    }

    private void onDownloadSuccessDelivery(final TRSFileDownloadHttpCallback callback, final File file) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onEnd();
                callback.onResponse(file);
            }
        });
    }

    public Handler getDelivery() {
        return mDelivery;
    }

    public OkHttpClient getClient() {
        return mOkHttpClient;
    }
}
