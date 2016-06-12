package com.zgh.offlinereaderdemo.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.zgh.offlinereader.util.WebViewHelper;
import com.zgh.offlinereaderdemo.R;
import com.zgh.offlinereaderdemo.view.ProgressWebView;

public class WebViewActivity extends AppCompatActivity {
    public static final String KEY_URL="key_url";
    private ProgressWebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        webView= (ProgressWebView) findViewById(R.id.webview);
        String url = getIntent().getStringExtra(KEY_URL);
        WebViewHelper.setWebViewConfig(webView);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.loadUrl(url);
    }
}
