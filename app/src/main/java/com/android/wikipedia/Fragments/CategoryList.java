package com.android.wikipedia.Fragments;
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
//import com.android.wikipedia.Adapter.CategoryListAdapter;
import com.android.wikipedia.R;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;


import java.io.File;
import java.util.ArrayList;
@SuppressWarnings("ALL")
public class CategoryList extends Fragment {
    //CategoryListAdapter adapter;
    ArrayList<File> arrayList;
    ShimmerRecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v;
        v = layoutInflater.inflate(R.layout.category_list_fragment, viewGroup, false);
        recyclerView=v.findViewById(R.id.download_rv);
        refreshLayout=v.findViewById(R.id.swipeRefresh2);
        arrayList=new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        refreshLayout.setOnRefreshListener(() -> {
            loadCategory();
            refreshLayout.setRefreshing(false);
        });
        return v;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()){
            loadData();
        }
    }

    public void loadData() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE },23);
        }else {
            loadCategory();
        }
    }

    private void loadCategory() {
        recyclerView.hideShimmerAdapter();
        String categoryUrl="https://en.wikipedia.org/w/api.php?action=query&list=allcategories&acprefix=List%20of&formatversion=2";
        RequestQueue requestQueue;
        requestQueue= Volley.newRequestQueue(requireActivity());
        StringRequest request=new StringRequest(Request.Method.GET, categoryUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //JSONObject jsonObject=new JSONObject(response);
                Log.d("category =======", String.valueOf(response));

            }
        }, new Response.ErrorListener() {
            @SuppressLint ("LongLogTag")
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("category list:volley error=",error.getLocalizedMessage());
            }
        });
        requestQueue.add(request);
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
    public void search(String text) {

    }

}