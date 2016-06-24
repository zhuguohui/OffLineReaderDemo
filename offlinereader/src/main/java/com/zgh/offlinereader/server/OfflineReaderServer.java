package com.zgh.offlinereader.server;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;

import com.zgh.offlinereader.OffLineLevelItem;
import com.zgh.offlinereader.ui.OffLineProgressUI;
import com.zgh.offlinereader.util.LoadWebView;
import com.zgh.offlinereader.util.WebViewHelper;
import com.zgh.trshttp.TRSHttpUtil;
import com.zgh.trshttp.callback.TRSStringHttpCallback;
import com.zgh.trshttp.request.TRSHttpRequest;
import com.zgh.trshttp.util.ConfigUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhuguohui on 2016/5/19.
 */
public class OfflineReaderServer extends Service {
    private static final int MSG_DOWNLOADFINSH = 1;
    private LoadWebView mWebView = null;
    private static final String TAG = OfflineReaderServer.class.getSimpleName();
    private boolean isRunning = false;
    public Set<String> mNeedLoadUrlSet = new HashSet<>();
    private int nexIndex = 0;
    private android.support.v4.app.NotificationCompat.Builder builder;
    private static OffLineLevelItem sFirstLevelItem;
    private List<String> mNeedLoadUrlList = new ArrayList<>();
    private static boolean haveInit = false;
    private Object obj = new Object();
    private ExecutorService pool = Executors.newFixedThreadPool(5);
    int myLevels = 0;
    int threadCount = 0;
    WindowManager windowManager;
    WindowManager.LayoutParams params;

    public static void init(@NonNull Context context, @NonNull File cacheDir, @NonNull OffLineLevelItem firstLevelItem, @NonNull OffLineProgressUI progressUI) {
        ConfigUtil.init(context, cacheDir);
        setFirstLevel(firstLevelItem);
        setProgressUI(progressUI);
        haveInit = true;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (!haveInit) {
            throw new RuntimeException("请先调用init()方法，初始化OfflineReaderServer");
        }
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        params=new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.width = 1;
        params.height = 1;
        initWebView();
        windowManager.addView(mWebView,params);
    }

    private boolean downloading = false;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DOWNLOADFINSH:
                    //下载网页
                    downloadWebpages();
                    break;

            }
        }
    };




    public static void setFirstLevel(@NonNull OffLineLevelItem offLineLevelItem) {
        sFirstLevelItem = offLineLevelItem;
    }

    private void downloadWebpages() {
        nexIndex = 0;
        mNeedLoadUrlList.clear();
        mNeedLoadUrlList = new ArrayList<>(mNeedLoadUrlSet);
        if (mNeedLoadUrlSet.size() > 0) {
            mWebView.loadUrl(mNeedLoadUrlList.get(0));

        } else {
            closePro();
            stopSelf();
        }

    }


    private void initWebView() {

        if (mWebView == null) {
            mWebView = new LoadWebView(this);
            WebViewHelper.setWebViewConfig(mWebView);
            mWebView.setFinishListenter(new LoadWebView.OnLoadFinishListenter() {
                @Override
                public void onFinish() {

                    int index = mNeedLoadUrlList.indexOf(mWebView.getUrl());
                    if (index == -1) {
                        return;
                    }
                    int present = (int) (index * 100.0 / (mNeedLoadUrlSet.size() - 1));
                    Log.i("zzz", "onPageFinished present=" + present);
                    updatePro(present);
                    index++;
                    if (index <= mNeedLoadUrlSet.size() - 1) {
                        String nextUrl = mNeedLoadUrlList.get(index);
                        mWebView.loadUrl(nextUrl);
                    }
                    //离线完成
                    if (present == 100) {
                        closePro();
                        stopSelf();
                    }
                }

            });
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isRunning) {
            showPro();
            isRunning = true;
            mNeedLoadUrlSet.clear();
            myLevels = 0;
            threadCount = 0;
            downloadOffLineevel(sFirstLevelItem);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        windowManager.removeView(mWebView);
        isRunning = false;
        pool.shutdownNow();
    }

    public void downloadOffLineevel(final OffLineLevelItem item) {
        if (item == null) {
            if (item == sFirstLevelItem) {
                closePro();

            } else {
                synchronized (obj) {
                    myLevels--;
                }
                if (myLevels == 0 && threadCount == 0) {
                    onDownloadFinish();
                }
            }
            return;
        }

        if (item.haveNextLevel()) {
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    synchronized (obj) {
                        threadCount++;
                    }
                    TRSHttpRequest.Builder builder = new TRSHttpRequest.Builder();
                    TRSHttpRequest request = builder.url(item.getNextLevelListUrl()).build();
                    TRSHttpUtil.getInstance().loadString(request, new TRSStringHttpCallback() {
                        @Override
                        public void onResponse(String response) {

                            List<OffLineLevelItem> nextLevelList = item.getNextLevelList(response);
                            synchronized (this) {
                                threadCount--;
                                if (nextLevelList != null) {
                                    for (OffLineLevelItem item : nextLevelList) {
                                        if (!item.haveNextLevel()) {
                                            myLevels++;
                                        }
                                    }
                                }
                            }
                            if (nextLevelList != null) {
                                for (OffLineLevelItem item : nextLevelList) {
                                    downloadOffLineevel(item);
                                }
                            }
                        }

                        @Override
                        public void onError(String error) {

                            synchronized (obj) {
                                threadCount--;
                            }
                        }
                    });
                }
            });
        } else {
            mNeedLoadUrlSet.add(item.getWebUrl());
            synchronized (obj) {
                myLevels--;
            }
            if (myLevels == 0 && threadCount == 0) {
                onDownloadFinish();
            }

        }

    }

    //下载完成时调用
    private void onDownloadFinish() {
        handler.sendEmptyMessage(MSG_DOWNLOADFINSH);
    }

    private static OffLineProgressUI sProgressUI;

    public static void setProgressUI(@NonNull OffLineProgressUI progressUI) {
        sProgressUI = progressUI;
    }

    private static void showPro() {
        if (checkPro()) {
            sProgressUI.showProgress();
        }
    }

    private static void closePro() {
        if (checkPro()) {
            sProgressUI.closeProgress();
        }
    }

    private static void updatePro(int progress) {
        if (checkPro()) {
            sProgressUI.updateProgress(progress);
        }
    }

    private static boolean checkPro() {
        return sProgressUI != null;
    }

}
