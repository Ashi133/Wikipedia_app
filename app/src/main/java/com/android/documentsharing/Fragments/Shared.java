package com.android.documentsharing.Fragments;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.documentsharing.Activities.Users;
import com.android.documentsharing.Adapter.sharedAdapter;
import com.android.documentsharing.Holder.documentHolder;
import com.android.documentsharing.R;
import com.android.documentsharing.UpdateOnlineStatus;
import com.android.documentsharing.databinding.FragmentSharedBinding;
import com.android.documentsharing.getUri;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class Shared extends Fragment {
    sharedAdapter adapter;
    ArrayList<documentHolder> arrayList;
    ShimmerRecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    DatabaseReference reference;
    FirebaseAuth auth;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_shared, container, false);
        recyclerView=view.findViewById(R.id.shared_rv);
        refreshLayout=view.findViewById(R.id.swipeRefresh);
        auth=FirebaseAuth.getInstance();
        reference= FirebaseDatabase.getInstance().getReference().child("DocumentSharing").child("Documents")
        .child(Objects.requireNonNull(auth.getCurrentUser()).getUid()).child("shared");
        refreshLayout.setOnRefreshListener(() -> {
            loadData();
            refreshLayout.setRefreshing(false);
        });
        arrayList=new ArrayList<>();
        adapter=new sharedAdapter(requireActivity(),arrayList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(adapter);
        recyclerView.showShimmerAdapter();
        loadData();
        view.findViewById(R.id.addNew).setOnClickListener(view1 -> {
            Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            launcher.launch(intent);
        });
        return view;
    }
    ActivityResultLauncher<Intent> launcher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        Intent data=result.getData();
        if (data != null) {
            Uri uri=data.getData();
            Intent intent=new Intent(requireActivity(), Users.class);
            String path=getUri.getRealPath(requireActivity(),uri);
            intent.putExtra("uri",uri.toString());
            intent.putExtra("path",path);
            startActivity(intent);
        }
    });
    private void loadData() {
        if (!UpdateOnlineStatus.check_network_state(requireActivity())){
            Toast.makeText(requireActivity(), "Internet Connection error !", Toast.LENGTH_SHORT).show();
        }else{
            reference.addValueEventListener(new ValueEventListener() {
                @SuppressLint ("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    arrayList.clear();
                    if (snapshot.exists()){
                        for (DataSnapshot snapshot1:snapshot.getChildren()){
                            documentHolder holder=snapshot1.getValue(documentHolder.class);
                            arrayList.add(holder);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    recyclerView.hideShimmerAdapter();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

}