package com.zgh.trshttp.provider;


import com.zgh.trshttp.callback.TRSFileDownloadHttpCallback;
import com.zgh.trshttp.callback.TRSFileUploadHttpCallback;
import com.zgh.trshttp.callback.TRSStringHttpCallback;
import com.zgh.trshttp.request.TRSHttpRequest;
import com.zgh.trshttp.util.ConfigUtil;
import com.zgh.trshttp.util.TRSFileUtil;

/**
 * Created by Vincent Woo
 * Date: 2015/12/21
 * Time: 9:56
 */
public abstract class TRSBaseHttpProvider {
    public abstract void loadString(TRSHttpRequest request, TRSStringHttpCallback callback);

    public abstract void uploadFile(TRSHttpRequest request, TRSFileUploadHttpCallback callback);

    public abstract void downloadFile(TRSHttpRequest request, TRSFileDownloadHttpCallback callback);

    protected void loadLocalString(String path, final TRSStringHttpCallback callback) {
        try {
            String result = TRSFileUtil.getString(ConfigUtil.getContext(), path);
            callback.onResponse(result);
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }
}
