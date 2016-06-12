package com.zgh.offlinereader.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.PopupWindow;
import android.widget.SeekBar;

import com.zgh.trshttp.util.ConfigUtil;

import java.io.File;

/**
 * Created by yuelin on 2016/5/19.
 */
public class WebViewHelper {
    private static final String TAG = WebViewHelper.class.getSimpleName();
    private static final String APP_CACHE_DIRNAME = "/webcache"; // web缓存目录
    public static final String KEY_FONT_SIZE = "key_font_size";
    public static final String KEY_HAVE_SET_FONT_SIZE = "key_have_set_font_size";
    private static final int MAX_SIZE = 30 * 1024 * 1024;//最大缓存30M
    private static PopupWindow popupWindow;
    private static SeekBar seekBar;

    //配置webview的缓存目录
    public static void setWebViewConfig(@NonNull WebView mWebView) {
        //设置缓存路径
        setWebViewCachePath(mWebView);
    }

    public static void setWebViewCachePath(@NonNull WebView mWebView) {
        if (mWebView == null) {
            return;
        }
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        // 建议缓存策略为，判断是否有网络，有的话，使用LOAD_DEFAULT,无网络时，使用LOAD_CACHE_ELSE_NETWORK

        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); // 设置缓存模式
        // 开启DOM storage API 功能
        mWebView.getSettings().setDomStorageEnabled(true);
        // 开启database storage API功能
        mWebView.getSettings().setDatabaseEnabled(true);
        // String cacheDirPath = getFilesDir().getAbsolutePath()
        //         + APP_CACHE_DIRNAME;
        String cacheDirPath = ConfigUtil.getCacheDir()
                + APP_CACHE_DIRNAME;
        Log.i(TAG, "cachePath=" + cacheDirPath);
        // 设置数据库缓存路径
        mWebView.getSettings().setDatabasePath(cacheDirPath); // API 19 deprecated
        // 设置Application caches缓存目录
        mWebView.getSettings().setAppCachePath(cacheDirPath);
        // 开启Application Cache功能
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setAppCacheMaxSize(MAX_SIZE);
    }

    public static void learCache(Context context) {
        String cacheDirPath = ConfigUtil.getCacheDir()
                + APP_CACHE_DIRNAME;
        File file = new File(cacheDirPath);
        if (file.exists()) {
            deleteFile(file);
        }
        // 清理WebView缓存数据库
        try {
            context.deleteDatabase("webview.db");
            context.deleteDatabase("webviewCache.db");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void deleteFile(File file) {
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    deleteFile(f);
                }
                file.delete();
            } else {
                file.delete();
            }
        }
    }
}
