package com.android.wikipedia.Fragments;
import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.wikipedia.Adapter.ArticlesAdapter;
import com.android.wikipedia.Holder.Holder;
import com.android.wikipedia.R;
import com.android.wikipedia.contentLoader;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class Articles extends Fragment {
    ArticlesAdapter adapter;
    //ArrayList<Holder> arrayList;
    ShimmerRecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    ArrayList<Holder> arrayList;
    //FirebaseAuth auth;
    @SuppressLint ("LongLogTag")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.articles_fragment, container, false);
        recyclerView=view.findViewById(R.id.article_rv);
        refreshLayout=view.findViewById(R.id.swipeRefresh);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        arrayList=new ArrayList<>();
        adapter=new ArticlesAdapter(requireActivity(),arrayList);
        recyclerView.setAdapter(adapter);
        SwipeRefreshLayout refreshLayout=view.findViewById(R.id.swipeRefresh);
        loadArticle();
        refreshLayout.setOnRefreshListener(this::loadArticle);
        //String featuredImageUrl="https://commons.wikimedia.org/w/api.php?action=query&prop=imageinfo&iiprop=timestamp|user|url&generator=categorymembers&gcmtype=file&gcmtitle=Category:Featured_pictures_on_Wikimedia_Commons&format=json&utf8";
        //String categoryListUrl="https://en.wikipedia.org/w/api.php?action=query&list=allcategories&acprefix=List%20of&formatversion=2";

        return view;
    }

    @SuppressLint ("NotifyDataSetChanged")
    private void loadArticle() {
        recyclerView.showShimmerAdapter();
        //loading of article logic goes here.
        String articleUrl="https://en.wikipedia.org/w/api.php?format=json&action=query&generator=random&grnnamespace=0&prop=revisions|images&rvprop=content&grnlimit=10";
        fetchdata(articleUrl);














        refreshLayout.setRefreshing(false);
        new Handler().postDelayed(() -> recyclerView.hideShimmerAdapter(), 1500);
    }

    private void fetchdata(String url) {
        RequestQueue requestQueue;
        requestQueue= Volley.newRequestQueue(requireActivity());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("Response result=", String.valueOf(response));
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(response);
                    JSONObject object=jsonObject.getJSONObject("query").getJSONObject("pages");
                    JSONArray array=object.names();
                    JSONObject object1=object.getJSONObject(array.get(0).toString());
                    String title=object1.getString("title");
                    Log.d("Response result=", String.valueOf(object1));
                    Toast.makeText(requireActivity(), "Title="+title, Toast.LENGTH_LONG).show();



                    String content=object1.getString("*");
                    Toast.makeText(requireActivity(), "Content="+content, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                error.printStackTrace();
            }
        });
        requestQueue.add(stringRequest);
    }

    public  void search(String text){

    }
}