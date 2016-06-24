package com.zgh.offlinereader.util;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebView;

/**
 * Created by yuelin on 2016/6/24.
 */
public class MyWebView extends WebView {
    private boolean isRendered = false;
    private static final int MSG_FINISH=1;
    private static final int MIN_CONTENT_HEIGHT=1000;
    public MyWebView(Context context) {
        this(context, null);
    }
    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    private int contentHeight=MIN_CONTENT_HEIGHT;
    Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==MSG_FINISH) {
                if (finishListenter != null) {
                    finishListenter.onFinish();
                    contentHeight=MIN_CONTENT_HEIGHT;
                }
            }
        }
    };
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i("zzz", "contentHeight=" + getContentHeight());
        if(getContentHeight()>=contentHeight){
            contentHeight=getContentHeight();
            handler.removeMessages(MSG_FINISH);
            handler.sendEmptyMessageDelayed(MSG_FINISH,200);
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
