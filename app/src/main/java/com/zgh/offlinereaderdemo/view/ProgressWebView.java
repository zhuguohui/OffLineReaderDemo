package com.zgh.offlinereaderdemo.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.zgh.offlinereader.util.AppUtil;
import com.zgh.offlinereaderdemo.R;


/**
 * 带进度条的WebView
 * @author 农民伯伯
 * @see http://www.cnblogs.com/over140/archive/2013/03/07/2947721.html
 *
 */
@SuppressWarnings("deprecation")
public class ProgressWebView extends WebView {

    private ProgressBar progressbar;

    public ProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        progressbar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        progressbar.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, AppUtil.dip2px(context, 3), 0, 0));
        progressbar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_drawable));
        progressbar.setBackgroundColor(Color.WHITE);
        addView(progressbar);;
        setWebChromeClient(new WebChromeClient());
    }

    public class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressbar.setVisibility(GONE);
            } else {
                if (progressbar.getVisibility() == GONE)
                    progressbar.setVisibility(VISIBLE);
                progressbar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }

    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        progressbar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
        if(mOnscrollChangeListenter!=null){
            mOnscrollChangeListenter.onScrollChange(l-oldl,t-oldt);
        }

    }

    onScrollChangeListenter mOnscrollChangeListenter;
    public void setmOnscrollChangeListenter(onScrollChangeListenter listenter){
        mOnscrollChangeListenter=listenter;
    }

    public static interface onScrollChangeListenter{
       void onScrollChange(int dx, int dy);
    }
}