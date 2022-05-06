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
import com.android.wikipedia.Adapter.ArticlesAdapter;
import com.android.wikipedia.Holder.Holder;
import com.android.wikipedia.R;
import com.android.wikipedia.contentLoader;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import java.util.ArrayList;

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
        refreshLayout.setOnRefreshListener(() -> {
            loadArticle();
        });
        String featuredImageUrl="https://commons.wikimedia.org/w/api.php?action=query&prop=imageinfo&iiprop=timestamp|user|url&generator=categorymembers&gcmtype=file&gcmtitle=Category:Featured_pictures_on_Wikimedia_Commons&format=json&utf8";
        String articleUrl="https://en.wikipedia.org/w/api.php?format=json&action=query&generator=random&grnnamespace=0&prop=revisions|images&rvprop=content&grnlimit=10";
        String categoryListUrl="https://en.wikipedia.org/w/api.php?action=query&list=allcategories&acprefix=List%20of&formatversion=2";
        contentLoader.loadArticle(requireActivity(),articleUrl);
        return view;
    }

    @SuppressLint ("NotifyDataSetChanged")
    private void loadArticle() {
        recyclerView.showShimmerAdapter();
        //loading of article logic goes here.
        Holder holder=new Holder("Article","This is the first article");
        arrayList.add(holder);
        adapter.notifyDataSetChanged();














        refreshLayout.setRefreshing(false);
        new Handler().postDelayed(() -> recyclerView.hideShimmerAdapter(), 1500);
    }

    public  void search(String text){

    }
}