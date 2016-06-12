package com.zgh.offlinereaderdemo.app;

import android.app.Application;
import android.content.Intent;
import android.view.View;

import com.zgh.offlinereader.WaterWaveProgressUI;
import com.zgh.offlinereader.server.OfflineReaderServer;
import com.zgh.offlinereaderdemo.bean.MyFirstLevel;

/**
 * Created by zhuguohui on 2016/6/8.
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        OfflineReaderServer.init(this, getCacheDir(), new MyFirstLevel(),new WaterWaveProgressUI(this));
    }
}
