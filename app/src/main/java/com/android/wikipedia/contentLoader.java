package com.android.wikipedia;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class contentLoader {
    @SuppressLint ("StaticFieldLeak")
    public static Context mContext;
    public static void loadArticle(Context context, String url){
        mContext=context;
        loader(url);
    }
    public static void loadImage(Context context,String url){
        mContext=context;
    }
    public static void loadCategory(Context context,String url){
        mContext=context;
    }
    public static void loader(String url){

    }
}
