package com.android.wikipedia.Fragments;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.wikipedia.Adapter.FeaturedImageAdapter;

import com.android.wikipedia.R;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.firebase.database.DatabaseReference;

public class FeaturedImage extends Fragment {
    FeaturedImageAdapter adapter;
    //ArrayList<documentHolder> arrayList;
    ShimmerRecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    DatabaseReference reference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.featured_image_fragment, container, false);
        recyclerView=view.findViewById(R.id.receive_rv);
        refreshLayout=view.findViewById(R.id.swipeRefresh1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.hideShimmerAdapter();
        return view;
    }
    private void popUpNotification(String fName,String owner) {
        NotificationCompat.Builder builder=new NotificationCompat.Builder(requireActivity(), String.valueOf(123));
        builder.setSmallIcon(R.drawable.wikipedia);
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
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()){
            //load();
        }
    }

    public void search(String text) {

    }

}