package com.zgh.trshttp.callback;

/**
 * Created by Vincent Woo
 * Date: 2015/12/22
 * Time: 13:49
 */
public abstract class TRSFileUploadHttpCallback extends TRSBaseHttpCallback<String> {
    public abstract void onProgress(long byteCount, long contentLength, boolean done);
}
