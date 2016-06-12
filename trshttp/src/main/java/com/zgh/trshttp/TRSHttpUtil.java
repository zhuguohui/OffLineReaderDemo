package com.zgh.trshttp;


import com.zgh.trshttp.callback.TRSFileDownloadHttpCallback;
import com.zgh.trshttp.callback.TRSFileUploadHttpCallback;
import com.zgh.trshttp.callback.TRSStringHttpCallback;
import com.zgh.trshttp.provider.TRSBaseHttpProvider;
import com.zgh.trshttp.provider.okhttp.TRSOkHttpProvider;
import com.zgh.trshttp.request.TRSHttpRequest;

/**
 * Created by Vincent Woo
 * Date: 2015/12/21
 * Time: 9:52
 */
public class TRSHttpUtil {
    private static TRSHttpUtil mInstance;
    private TRSBaseHttpProvider mProvider;
    public static final int GET = 0;
    public static final int POST = 1;

    private TRSHttpUtil() {
        mProvider = new TRSOkHttpProvider();
    }

    public static TRSHttpUtil getInstance() {
        if (mInstance == null) {
            synchronized (TRSHttpUtil.class) {
                if (mInstance == null) {
                    mInstance = new TRSHttpUtil();
                }
            }
        }
        return mInstance;
    }

    /**
     *  Load String Data
     */
    public void loadString(TRSHttpRequest request, TRSStringHttpCallback callback) {
        mProvider.loadString(request, callback);
    }

    /**
     *  Post File to Server
     */
    public void uploadFile(TRSHttpRequest request, TRSFileUploadHttpCallback callback) {
        mProvider.uploadFile(request, callback);
    }

    /**
     *  Download File from Server
     */
    public void downloadFile(TRSHttpRequest request, TRSFileDownloadHttpCallback callback) {
        mProvider.downloadFile(request, callback);
    }
}
