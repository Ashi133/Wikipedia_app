package com.android.documentsharing.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.android.documentsharing.Adapter.downloadAdapter;
import com.android.documentsharing.R;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import java.io.File;
import java.util.ArrayList;
public class Downloaded extends Fragment {
    downloadAdapter adapter;
    ArrayList<File> arrayList;
    ShimmerRecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_downloaded, container, false);
        recyclerView=view.findViewById(R.id.download_rv);
        refreshLayout=view.findViewById(R.id.swipeRefresh2);
        arrayList=new ArrayList<>();
        adapter=new downloadAdapter(requireActivity(),arrayList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(adapter);
        loadData();
        refreshLayout.setOnRefreshListener(() -> {
            loadData();
            refreshLayout.setRefreshing(false);
        });
        return view;
    }

    private void loadData() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE },23);
        }else {
            //recyclerView.showShimmerAdapter();
            //loading downloaded file here.
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 23){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                loadData();
            }else {
                Toast.makeText(requireActivity(), "Permission required!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}