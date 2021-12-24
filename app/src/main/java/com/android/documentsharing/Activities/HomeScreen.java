package com.android.documentsharing.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.android.documentsharing.Adapter.ViewPagerAdapter;
import com.android.documentsharing.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

@SuppressWarnings ("ALL")
public class HomeScreen extends AppCompatActivity {
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    BottomNavigationView navigationView;
    @Override
    protected void onCreate(Bundle saved){
        super.onCreate(saved);
        setContentView(R.layout.activity_home_screen);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.share_app:
                break;
            case R.id.user_setting:
                break;
            case R.id.contact_us:
                break;
            case R.id.log_out:
                break;
            case R.id.delete_account:
                break;
        }
        return true;
    }

}