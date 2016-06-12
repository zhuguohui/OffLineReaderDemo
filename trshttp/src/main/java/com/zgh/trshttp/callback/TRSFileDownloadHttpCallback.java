package com.zgh.trshttp.callback;

import java.io.File;

/**
 * Created by Vincent Woo
 * Date: 2015/12/22
 * Time: 14:02
 */
public abstract class TRSFileDownloadHttpCallback extends TRSBaseHttpCallback<File> {
    public abstract void onProgress(long byteCount, long contentLength, boolean done);
}
