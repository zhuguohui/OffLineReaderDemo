package com.zgh.trshttp.util;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.File;

/**
 * Created by yuelin on 2016/6/8.
 */
public class ConfigUtil {
    private static Context sContext = null;
    private static File sCacheDir = null;

    public static void init(@NonNull Context context, @NonNull File cacheDir) {
        sContext = context;
        sCacheDir = cacheDir;
    }

    public static Context getContext() {
        if (sContext == null) {
            throw new RuntimeException("请先调用init()方法进行初始化");
        }
        return sContext;
    }

    public static File getCacheDir() {
        if (sCacheDir == null) {
            throw new RuntimeException("请先调用init()方法进行初始化");
        }
        return sCacheDir;
    }
}
