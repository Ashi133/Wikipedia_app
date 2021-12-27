package com.android.documentsharing.Fragments;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.documentsharing.Adapter.sharedAdapter;
import com.android.documentsharing.Holder.documentHolder;
import com.android.documentsharing.R;
import com.android.documentsharing.UpdateOnlineStatus;
import com.android.documentsharing.databinding.FragmentSharedBinding;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;

import java.util.ArrayList;
import java.util.Objects;

public class Shared extends Fragment {
    sharedAdapter adapter;
    ArrayList<documentHolder> arrayList;
    ShimmerRecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_shared, container, false);
        recyclerView=view.findViewById(R.id.shared_rv);
        refreshLayout=view.findViewById(R.id.swipeRefresh);
        refreshLayout.setOnRefreshListener(() -> {
            loadData();
            refreshLayout.setRefreshing(false);
        });
        arrayList=new ArrayList<>();
        adapter=new sharedAdapter(requireActivity(),arrayList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(adapter);
        recyclerView.hideShimmerAdapter();
        return view;
    }
    private void loadData() {
        if (!UpdateOnlineStatus.check_network_state(requireActivity())){
            Toast.makeText(requireActivity(), "Internet Connection error !", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(requireActivity(), "Connection available", Toast.LENGTH_SHORT).show();
        }
    }

}