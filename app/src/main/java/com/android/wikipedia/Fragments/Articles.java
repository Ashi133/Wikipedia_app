package com.android.wikipedia.Fragments;
import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.wikipedia.Adapter.ArticlesAdapter;
import com.android.wikipedia.R;
import com.android.wikipedia.UpdateTheme;
import com.android.wikipedia.contentLoader;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.firebase.database.DatabaseReference;

public class Articles extends Fragment {
    ArticlesAdapter adapter;
    //ArrayList<documentHolder> arrayList;
    ShimmerRecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    DatabaseReference reference;
    //FirebaseAuth auth;
    @SuppressLint ("LongLogTag")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.articles_fragment, container, false);
        recyclerView=view.findViewById(R.id.shared_rv);
        refreshLayout=view.findViewById(R.id.swipeRefresh);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.hideShimmerAdapter();
        String url="https://commons.wikimedia.org/w/api.php?action=query&prop=imageinfo&iiprop=timestamp|user|url&generator=categorymembers&gcmtype=file&gcmtitle=Category:Featured_pictures_on_Wikimedia_Commons&format=json&utf8";
        contentLoader.loadArticle(requireActivity(),url);
        return view;
    }
    public  void search(String text){

    }
}