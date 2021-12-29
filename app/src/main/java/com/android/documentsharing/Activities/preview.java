package com.android.documentsharing.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.documentsharing.databinding.ActivityPreviewBinding;

public class preview extends AppCompatActivity {
    ActivityPreviewBinding binding;
    WebView webView;
    String extension,url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initializeWebView();
    }

    @SuppressLint ("SetJavaScriptEnabled")
    private void initializeWebView() {
        webView=binding.webView;
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.setVisibility(View.VISIBLE);
    }


}