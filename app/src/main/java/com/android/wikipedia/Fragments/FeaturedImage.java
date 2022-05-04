package com.android.wikipedia.Fragments;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.android.wikipedia.Adapter.FeaturedImageAdapter;
import com.android.wikipedia.Holder.documentHolder;
import com.android.wikipedia.R;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import javax.crypto.NoSuchPaddingException;

public class FeaturedImage extends Fragment {
    FeaturedImageAdapter adapter;
    ArrayList<documentHolder> arrayList;
    ShimmerRecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    DatabaseReference reference;
    FirebaseAuth auth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_received, container, false);
        recyclerView=view.findViewById(R.id.receive_rv);
        refreshLayout=view.findViewById(R.id.swipeRefresh1);
        auth=FirebaseAuth.getInstance();
        reference= FirebaseDatabase.getInstance().getReference().child("DocumentSharing").child("Documents")
                .child(Objects.requireNonNull(auth.getCurrentUser()).getUid()).child("received");
        arrayList=new ArrayList<>();
        adapter=new FeaturedImageAdapter(requireActivity(),arrayList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(adapter);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()){
                    documentHolder holder=snapshot.getValue(documentHolder.class);
                    if (holder != null){
                        String file=holder.getName();
                        String owner=holder.getOwnerName();
                        if (holder.isNew()){
                            MediaPlayer player=MediaPlayer.create(requireActivity(),R.raw.received);
                            player.start();
                            popUpNotification(file,owner);
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        refreshLayout.setOnRefreshListener(() -> {
            load();
            refreshLayout.setRefreshing(false);
        });
        return view;
    }
    private void popUpNotification(String fName,String owner) {
        NotificationCompat.Builder builder=new NotificationCompat.Builder(requireActivity(), String.valueOf(123));
        builder.setSmallIcon(R.drawable.sharing);
        builder.setContentTitle("Received Document");
        builder.setContentText(owner +"shared a file "+fName +" with you!");
        builder.setAutoCancel(false);
        builder.setPriority(Notification.PRIORITY_DEFAULT);
        Notification notification=builder.build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel("123","my notification", NotificationManager.IMPORTANCE_DEFAULT);
            //channel.setDescription("My notification");
            NotificationManager manager=(NotificationManager) requireActivity().getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
        NotificationManagerCompat compat = NotificationManagerCompat.from(requireActivity());
        compat.notify(123,notification);
    }
    private void load() {
        if (!UpdateOnlineStatus.check_network_state(requireActivity())){
            Toast.makeText(requireActivity(), "Internet Connection error !", Toast.LENGTH_SHORT).show();
        }else{
            recyclerView.showShimmerAdapter();
            reference.addValueEventListener(new ValueEventListener() {
                @SuppressLint ("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    arrayList.clear();
                    if (snapshot.exists()){
                        for (DataSnapshot snapshot1:snapshot.getChildren()){
                            documentHolder holder=snapshot1.getValue(documentHolder.class);
                            try {
                                String url= Security.Decrypt(holder.getUrl());
                                holder.setUrl(url);
                                arrayList.add(holder);
                            } catch (NoSuchPaddingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                    new Handler().postDelayed(() -> recyclerView.hideShimmerAdapter(), 1000);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()){
            load();
        }
    }

    public void search(String text) {
        ArrayList<documentHolder> temp=new ArrayList<>();
        if (text.isEmpty()){
            temp.addAll(arrayList);
        }else {
            for (documentHolder holder:arrayList){
                if (holder.getName().toLowerCase().contains(text)){
                    temp.add(holder);
                }
            }
        }
        adapter.updateList(temp);
    }

}