package com.zgh.trshttp.callback;

/**
 * Created by Vincent Woo
 * Date: 2015/12/21
 * Time: 10:17
 */



public abstract class TRSBaseHttpCallback<T> {
    public void onStart() {}
    public void onEnd() {}

    public abstract void onResponse(T response);
    public abstract void onError(String error);
}
