package com.android.documentsharing.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.net.Uri;
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
    String extension,url,name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initializeWebView();
        name=getIntent().getStringExtra("name");
        extension=getIntent().getStringExtra("ext");
        url=getIntent().getStringExtra("url");
        if (name != null){
            setTitle(name);
        }else {
            setTitle("Preview");
        }
        load(url,extension);//loading document inside webView.
    }

    private void load(String url, String extension) {
        if (extension.equals("pdf") ||
                extension.equals("docx") ||
                extension.equals("xlsx") ||
                extension.equals("jpg") ||
        extension.equals("png")){
            String url1= Uri.encode(url);
            String finalUrl="https://docs.google.com/viewer?url="+url1+"&embedded=true";
            webView.loadUrl(finalUrl);
        }else {
            webView.loadUrl("https://www.google.com");
        }
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
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
        }else {
            super.onBackPressed();
        }
    }

}