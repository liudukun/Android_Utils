package com.liudukun.dkchat.utils;

import android.os.Bundle;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.liudukun.dkchat.R;
import com.liudukun.dkchat.base.BaseActivity;

public class DKWebActivity extends BaseActivity implements View.OnClickListener {
    public static String url;
    WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        navigationBar = findViewById(R.id.navigationBar);


        navigationBar.backListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        };

        navigationBar.moreListener = this;

        setTitle("");

        webView = findViewById(R.id.webView);

        webView.loadUrl(url);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setTitle(view.getTitle());
                    }
                });
            }
        });

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

            }

        });

        webView.setFindListener(new WebView.FindListener() {
            @Override
            public void onFindResultReceived(int i, int i1, boolean b) {

            }
        });

    }

    @Override
    public void onClick(View v) {

    }


}
