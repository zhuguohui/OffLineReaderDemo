package com.zgh.offlinereader.util;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Created by yuelin on 2016/6/24.
 */
public class MyWebView extends WebView {
    public MyWebView(Context context) {
        this(context,null);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(finishListenter!=null){
            finishListenter.onFinish();
        }
    }

    public interface OnLoadFinishListenter{
        void onFinish();
    }

    private OnLoadFinishListenter finishListenter;

    public void setFinishListenter(OnLoadFinishListenter listenter){
        finishListenter=listenter;
    }
}
