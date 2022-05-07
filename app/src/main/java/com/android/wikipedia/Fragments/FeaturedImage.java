package com.android.wikipedia.Fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;
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

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.wikipedia.Adapter.FeaturedImageAdapter;

import com.android.wikipedia.Database.DatabaseManager;
import com.android.wikipedia.Database.Entities;
import com.android.wikipedia.Holder.*;
import com.android.wikipedia.R;
import com.android.wikipedia.*;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class FeaturedImage extends Fragment {
    FeaturedImageAdapter adapter;
    ArrayList<Holder> arrayList;
    ShimmerRecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    DatabaseManager databaseManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.featured_image_fragment, container, false);
        databaseManager=DatabaseManager.getINSTANCE(requireActivity());
        recyclerView=view.findViewById(R.id.featuredImage_rv);
        refreshLayout=view.findViewById(R.id.swipeRefresh1);
        recyclerView.setHasFixedSize(true);
        arrayList=new ArrayList<>();
        adapter=new FeaturedImageAdapter(requireActivity(),arrayList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        refreshLayout.setOnRefreshListener(this::loadImage);
        return view;
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()){
            loadImage();
        }
    }

    @SuppressLint ("NotifyDataSetChanged")
    private void loadImage() {
        recyclerView.showShimmerAdapter();
        //load image logic goes here.
        arrayList.clear();
        Holder holder=new Holder();
        holder.setTitle("Flower");
        String featuredImageUrl="https://commons.wikimedia.org/w/api.php?action=query&prop=imageinfo&iiprop=timestamp|user|url&generator=categorymembers&gcmtype=file&gcmtitle=Category:Featured_pictures_on_Wikimedia_Commons&format=json&utf8";
        if (UpdateTheme.check_network_state(requireActivity())){
            fetchdata(featuredImageUrl);
        }else{
            loadFromDatabase();
        }
        new Handler().postDelayed(() -> recyclerView.hideShimmerAdapter(), 1500);
        refreshLayout.setRefreshing(true);
    }

    @SuppressLint ("NotifyDataSetChanged")
    private void loadFromDatabase() {
        arrayList.clear();
        ArrayList<Entities> temp = new ArrayList<>(databaseManager.dao().getAllData());
        for (Entities entities:temp){
            Holder holder=new Holder();
            holder.setTitle(entities.getTitle());
            holder.setPath(entities.getUrl());
            arrayList.add(holder);
        }
        refreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged();
    }

    public void search(String text) {
        ArrayList<Holder> temp=new ArrayList<>();
        if (text.length()>0){
            for (Holder holder:arrayList){
                if (holder.getTitle().toLowerCase().contains(text.toLowerCase())){
                    temp.add(holder);
                }
            }
        }else {
            temp.addAll(arrayList);
        }
        adapter.update(temp);
    }
    private void fetchdata(String url) {
        RequestQueue requestQueue;
        requestQueue= Volley.newRequestQueue(requireActivity());
        @SuppressLint ("NotifyDataSetChanged") StringRequest stringRequest=new StringRequest(Request.Method.GET, url, response -> {
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(response);
                Log.d("Image=",response);
                JSONObject object=jsonObject.getJSONObject("query").getJSONObject("pages");
                JSONArray array=object.names();
                for (int i = 0; i< Objects.requireNonNull(array).length(); i++){
                    JSONObject object1=object.getJSONObject(array.get(i).toString());
                    String title=object1.getString("title");
                    JSONArray jsonArray= object1.getJSONArray("imageinfo");
                    JSONObject jsonObject1= (JSONObject) jsonArray.get(0);
                    String url1 =jsonObject1.getString("url");
                    String descriptionUrl=jsonObject1.getString("descriptionurl");
                    Holder holder=new Holder();
                    holder.setTitle(title);
                    holder.setUrl(url1);
                    holder.setDescriptionUrl(descriptionUrl);
                    arrayList.add(holder);

                }
                adapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            } catch (JSONException e) {
                e.printStackTrace();
                refreshLayout.setRefreshing(false);
            }
        }, error -> Toast.makeText(requireActivity(), "featuredImage:volley response error="+error.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
        requestQueue.add(stringRequest);
    }
}