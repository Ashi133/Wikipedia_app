package com.android.wikipedia.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.wikipedia.databinding.ActivityPreviewBinding;

public class preview extends AppCompatActivity {
    ActivityPreviewBinding binding;
    @RequiresApi (api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initializeWebView();
        String url=getIntent().getStringExtra("url");
        String title=getIntent().getStringExtra("title");
        if (title.length()>0){
            this.setTitle(title);
        }else {
            this.setTitle("Preview");
        }
        if (url.length()>0){
            binding.webView.loadUrl(url);
        }
    }

    @RequiresApi (api = Build.VERSION_CODES.O)
    @SuppressLint ("SetJavaScriptEnabled")
    private void initializeWebView() {
        WebView webView=binding.webView;
        webView.getSettings().setSafeBrowsingEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setDisplayZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.setWebViewClient(new WebViewClient());
    }

    @Override
    public void onBackPressed() {
        if (binding.webView.canGoBack()){
            binding.webView.goBack();
        }else {
            super.onBackPressed();
        }
    }

}