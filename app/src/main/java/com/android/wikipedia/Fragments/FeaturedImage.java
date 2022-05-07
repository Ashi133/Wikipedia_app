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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.wikipedia.Adapter.FeaturedImageAdapter;

import com.android.wikipedia.Holder.Holder;
import com.android.wikipedia.R;
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.featured_image_fragment, container, false);
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
        fetchdata(featuredImageUrl);
        new Handler().postDelayed(() -> recyclerView.hideShimmerAdapter(), 1500);
        refreshLayout.setRefreshing(true);
    }

    public void search(String text) {

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
                    //Log.d("des===",descriptionUrl);
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