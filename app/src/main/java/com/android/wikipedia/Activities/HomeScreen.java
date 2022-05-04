package com.android.wikipedia.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.wikipedia.Adapter.ViewPagerAdapter;
import com.android.wikipedia.Fragments.CategoryList;
import com.android.wikipedia.Fragments.FeaturedImage;
import com.android.wikipedia.Fragments.Articles;
import com.android.wikipedia.Holder.documentHolder;
import com.android.wikipedia.R;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;

@SuppressWarnings ("ALL")
public class HomeScreen extends AppCompatActivity {
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    BottomNavigationView navigationView;
    ValueEventListener listener;
    DatabaseReference reference;
    FirebaseAuth auth;
    DatabaseReference database=FirebaseDatabase.getInstance().getReference().child("DocumentSharing");
    StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Documents");
    int count=0;
    ArrayList<documentHolder> arrayList;

    @Override
    protected void onCreate(Bundle saved){
        super.onCreate(saved);
        setContentView(R.layout.activity_home_screen);
        auth=FirebaseAuth.getInstance();
        arrayList=new ArrayList<>();
        viewPager=findViewById(R.id.viewPager);
        viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setHorizontalScrollBarEnabled(true);
        navigationView=findViewById(R.id.bottom_navigation);
        navigationView.setItemHorizontalTranslationEnabled(true);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.shared_item:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.received_item:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.downloaded_item:
                        viewPager.setCurrentItem(2);
                        break;
                }
                return true;
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        navigationView.getMenu().findItem(R.id.shared_item).setChecked(true);
                        break;
                    case 1:
                        navigationView.getMenu().findItem(R.id.received_item).setChecked(true);
                        break;
                    case 2:
                        navigationView.getMenu().findItem(R.id.downloaded_item).setChecked(true);
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu,menu);
        MenuItem item=menu.findItem(R.id.search);
        SearchView searchView=(SearchView)item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText.toLowerCase());
                return false;
            }
        });
        return true;
    }

    private void filter(String text) {
        count=viewPager.getCurrentItem();
        try {
            if (count == 0){
                Articles fragment= (Articles) viewPager.getAdapter().instantiateItem(viewPager,count);
                fragment.search(text.toLowerCase());
            }else if (count == 1){
                FeaturedImage fragment= (FeaturedImage) viewPager.getAdapter().instantiateItem(viewPager,count);
                fragment.search(text.toLowerCase());
            }else {
                CategoryList fragment= (CategoryList) viewPager.getAdapter().instantiateItem(viewPager,count);
                fragment.search(text.toLowerCase());
            }
        }catch (Exception e){
            Log.e("Home : search ====",e.getLocalizedMessage());
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.settings:
                new Intent(HomeScreen.this,Settings.class);
                break;
        }
        return true;
    }
}