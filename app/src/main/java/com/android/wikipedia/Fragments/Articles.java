package com.android.wikipedia.Fragments;
import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.wikipedia.Adapter.ArticlesAdapter;
import com.android.wikipedia.Database.DatabaseManager;
import com.android.wikipedia.Database.Entities;
import com.android.wikipedia.Holder.Holder;
import com.android.wikipedia.R;
import com.android.wikipedia.UpdateTheme;
import com.android.wikipedia.downloadFile;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import javax.xml.xpath.XPath;

public class Articles extends Fragment {
    ArticlesAdapter adapter;
    ShimmerRecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    ArrayList<Holder> arrayList;
    DatabaseManager databaseManager;
    @SuppressLint ("LongLogTag")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.articles_fragment, container, false);
        databaseManager=DatabaseManager.getINSTANCE(requireActivity());
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
        return view;
    }

    @SuppressLint ("NotifyDataSetChanged")
    private void loadArticle() {
        recyclerView.showShimmerAdapter();
        //loading of article logic goes here.
        String articleUrl="https://en.wikipedia.org/w/api.php?format=json&action=query&generator=random&grnnamespace=0&prop=revisions|images&rvprop=content&grnlimit=10";
        if (UpdateTheme.check_network_state(requireActivity())){
            fetchdata(articleUrl);
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
            holder.setUrl(entities.getUrl());
            arrayList.add(holder);
        }
        refreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged();
    }

    private void fetchdata(String url) {
        arrayList.clear();
        RequestQueue requestQueue;
        requestQueue= Volley.newRequestQueue(requireActivity());
        @SuppressLint ("NotifyDataSetChanged") StringRequest stringRequest=new StringRequest(Request.Method.GET, url, response -> {
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(response);
                JSONObject object=jsonObject.getJSONObject("query").getJSONObject("pages");
                JSONArray array=object.names();
                for (int i = 0; i< Objects.requireNonNull(array).length(); i++){
                    JSONObject object1=object.getJSONObject(array.get(i).toString());
                    String title=object1.getString("title");
                    JSONArray jsonArray=object1.getJSONArray("revisions");
                    JSONObject jsonObject1= (JSONObject) jsonArray.get(0);
                    String content=jsonObject1.getString("*");
                    Holder holder=new Holder(title,content);
                    Entities entities=new Entities(title,content);
                    databaseManager.dao().insertData(entities);
                    arrayList.add(holder);
                }
                adapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            } catch (JSONException e) {
                refreshLayout.setRefreshing(false);
                e.printStackTrace();
            }
        }, error -> Toast.makeText(requireActivity(), "Article fragment:Volley error="+error.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
        requestQueue.add(stringRequest);
    }

    public  void search(String text){
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
}