package com.android.wikipedia.Fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.wikipedia.Adapter.FeaturedImageAdapter;

import com.android.wikipedia.Holder.Holder;
import com.android.wikipedia.R;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;

import java.util.ArrayList;

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
        Holder holder=new Holder();
        holder.setTitle("Flower");
        holder.setUrl("https://hgtvhome.sndimg.com/content/dam/images/hgtv/stock/2018/3/2/shutterstock_anemone-134595248.jpg.rend.hgtvcom.966.644.suffix/1519931799331.jpeg");
        arrayList.add(holder);
        adapter.notifyDataSetChanged();






        refreshLayout.setRefreshing(false);
        new Handler().postDelayed(() -> recyclerView.hideShimmerAdapter(), 1500);
    }

    public void search(String text) {

    }

}