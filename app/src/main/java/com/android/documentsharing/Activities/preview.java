package com.android.documentsharing.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.documentsharing.R;
import com.android.documentsharing.databinding.ActivityPreviewBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class preview extends AppCompatActivity {
    ActivityPreviewBinding binding;
    WebView webView;
    String extension,url,name;
    ArrayList<String> arrayList,arrayList1,arrayList2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initializeWebView();
        name=getIntent().getStringExtra("name");
        extension=getIntent().getStringExtra("ext");
        url=getIntent().getStringExtra("url");
        binding.textView.setMovementMethod(new ScrollingMovementMethod());
        if (name != null){
            setTitle(name);
        }else {
            setTitle("Preview");
        }
        load(url,extension);//loading document inside webView.
    }

    private void load(String url, String extension) {
        if (arrayList.contains(extension)){
            String url1= Uri.encode(url);
            String finalUrl="https://docs.google.com/viewer?url="+url1+"&embedded=true";
            webView.loadUrl(finalUrl);
        }else if (extension.equals("jpg") ||
                extension.equals("png") || extension.equals("jpeg")){
            webView.setVisibility(View.GONE);
            Glide.with(preview.this)
                    .load(Uri.parse(url))
                    .centerCrop()
                    .placeholder(R.drawable.user)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Toast.makeText(preview.this, "Unable to load file due to : -"+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            webView.loadUrl("https://www.google.com");
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            binding.lottieAnimation.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(binding.imgPrev);
        }else if (arrayList1.contains(extension)){
            webView.setVisibility(View.GONE);
            binding.imgPrev.setVisibility(View.GONE);
            new Thread(() -> {
                ArrayList<String> lines=new ArrayList<>();//to read each lines.
                try {
                    URL url1=new URL(url);
                    HttpURLConnection connection=(HttpURLConnection) url1.openConnection();
                    connection.setConnectTimeout(60000);
                    BufferedReader in=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String string;
                    while ((string = in.readLine()) != null){
                        lines.add(string);
                    }
                    in.close();
                }catch (Exception e){
                    webView.setVisibility(View.VISIBLE);
                    binding.lottieAnimation.setVisibility(View.GONE);
                    Toast.makeText(preview.this, "Unable to load file due to : -"+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    webView.loadUrl("https://www.google.com");
                }
                binding.textView.setText("");
                runOnUiThread(() -> {
                    for (String s:lines){
                        binding.textView.append(s+"\n");
                    }
                    binding.lottieAnimation.setVisibility(View.GONE);
                });
            }).start();
        }else if (arrayList2.contains(extension)){
            webView.loadUrl(url);
        }
        else {
            Toast.makeText(preview.this, "Preview not available for this file", Toast.LENGTH_LONG).show();
            webView.loadUrl("https://www.google.com");
        }
    }

    @SuppressLint ("SetJavaScriptEnabled")
    private void initializeWebView() {
        arrayList=new ArrayList<>();
        arrayList1=new ArrayList<>();
        arrayList2=new ArrayList<>();
        arrayList.add("pdf");//documents files type.
        arrayList.add("docx");
        arrayList.add("doc");
        arrayList.add("xlsx");
        arrayList.add("csv");
        arrayList1.add("c");//text files type.
        arrayList1.add("java");
        arrayList1.add("cpp");
        arrayList1.add("json");
        arrayList1.add("py");
        arrayList1.add("txt");
        arrayList1.add("kt");
        arrayList1.add("html");
        arrayList1.add("css");
        arrayList1.add("js");
        arrayList1.add("vbs");
        arrayList1.add("xml");
        arrayList1.add("sh");
        arrayList2.add("mp3");//media files
        arrayList2.add("mp4");
        arrayList2.add("m4a");
        arrayList2.add("wav");
        arrayList2.add("flac");
        arrayList2.add("fev");
        arrayList2.add("omg");
        arrayList2.add("igp");
        arrayList2.add("wow");
        arrayList2.add("vlc");
        arrayList2.add("ogg");
        arrayList2.add("ac3");
        arrayList2.add("amf");
        arrayList2.add("mov");
        arrayList2.add("wmv");
        arrayList2.add("flv");
        arrayList2.add("avi");
        arrayList2.add("webm");
        arrayList2.add("m4p");
        arrayList2.add("m4v");
        arrayList2.add("avchd");
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
        webView.getSettings().setMediaPlaybackRequiresUserGesture(true);
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