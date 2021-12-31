package com.android.documentsharing.Fragments;

import static androidx.core.util.ObjectsCompat.requireNonNull;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.android.documentsharing.Adapter.downloadAdapter;
import com.android.documentsharing.Holder.documentHolder;
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
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v;
        v = layoutInflater.inflate(R.layout.fragment_downloaded, viewGroup, false);
        recyclerView=v.findViewById(R.id.download_rv);
        refreshLayout=v.findViewById(R.id.swipeRefresh2);
        arrayList=new ArrayList<>();
        adapter=new downloadAdapter(requireActivity(),arrayList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(() -> {
            loadData();
            refreshLayout.setRefreshing(false);
        });
        return v;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            loadData();
        }
    }
    private void loadData() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE },23);
        }else {
            recyclerView.showShimmerAdapter();
            //loading downloaded file here.
            findFiles();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 23){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                loadData();
            }else {
                Toast.makeText(requireActivity(), "Permission required!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @SuppressLint ("NotifyDataSetChanged")
    public void findFiles() {
        arrayList.clear();
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "Document Sharing");
        if (!file.exists() && ! file.isDirectory()){
            if (file.mkdir()){
                Toast.makeText(requireActivity(), "Folder Document Sharing created successfully!", Toast.LENGTH_SHORT).show();
            }
        }
        File[] files = file.listFiles();
        if (files != null){
            for (File file1 : requireNonNull(files)) {
                if (!file1.isHidden()) {
                    arrayList.add(file1);
                }
            }
        }
        adapter.notifyDataSetChanged();
        new Handler().postDelayed(() -> {
            Toast.makeText(requireActivity(), "File Loaded Successfully!", Toast.LENGTH_SHORT).show();
            recyclerView.hideShimmerAdapter();
        }, 1000);
    }

    public void search(String text) {
        ArrayList<File> temp=new ArrayList<>();
        if (text.isEmpty()){
            temp.addAll(arrayList);
        }else {
            for (File holder:arrayList){
                if (holder.getName().toLowerCase().contains(text)){
                    temp.add(holder);
                }
            }
        }
        adapter.updateList(temp);
    }

}