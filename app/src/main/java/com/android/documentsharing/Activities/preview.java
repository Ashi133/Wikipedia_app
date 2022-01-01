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
import android.widget.Toast;

import com.android.documentsharing.R;
import com.android.documentsharing.databinding.ActivityPreviewBinding;
import com.bumptech.glide.Glide;

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
                extension.equals("xlsx")){
            String url1= Uri.encode(url);
            String finalUrl="https://docs.google.com/viewer?url="+url1+"&embedded=true";
            webView.loadUrl(finalUrl);
        }else if (extension.equals("jpg") ||
                extension.equals("png") || extension.equals("jpeg")){
            webView.setVisibility(View.GONE);
            binding.lottieAnimation.setVisibility(View.GONE);
            Glide.with(preview.this)
                    .load(Uri.parse(url))
                    .centerCrop()
                    .placeholder(R.drawable.user)
                    .into(binding.imgPrev);
        }
        else {
            Toast.makeText(preview.this, "Preview not available for this file", Toast.LENGTH_SHORT).show();
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
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                //super.onPageFinished(view, url);
                webView.setVisibility(View.VISIBLE);
                binding.lottieAnimation.setVisibility(View.GONE);
            }
        });
        webView.setWebChromeClient(new WebChromeClient());
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