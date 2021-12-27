package com.android.documentsharing.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.documentsharing.Adapter.ViewPagerAdapter;
import com.android.documentsharing.R;
import com.android.documentsharing.databinding.DeleteAccountBinding;
import com.android.documentsharing.databinding.LogoutBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

@SuppressWarnings ("ALL")
public class HomeScreen extends AppCompatActivity {
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    BottomNavigationView navigationView;
    @Override
    protected void onCreate(Bundle saved){
        super.onCreate(saved);
        setContentView(R.layout.activity_home_screen);
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
        Intent intent;
        switch (item.getItemId()){
            case R.id.share_app:
                intent=new Intent("android.intent.action.SEND");
                intent.setType("text/plain");
                intent.putExtra("android.intent.extra.SUBJECT","Document Sharing App");
                intent.putExtra("android.intent.extra.TEXT","This application help you to share document all over the world without any restriction and secured manner\nTo get this application click the link given below:-\n"+"https://tinyurl.com/y5vaqr8f"+"\n I hope it will helpful for you!\nThank you!");
                startActivity(Intent.createChooser(intent,"Share with"));
                break;
            case R.id.user_setting:
                break;
            case R.id.contact_us:
                intent=new Intent(this,Contact_Us.class);
                startActivity(intent);
                break;
            case R.id.log_out:
                LogoutBinding view1=LogoutBinding.inflate(getLayoutInflater());
                AlertDialog dialog=new AlertDialog.Builder(this)
                        .setCancelable(false).setView(view1.getRoot()).create();
                dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();
                view1.logOutNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                view1.logOutYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //log out code goes here...
                        ProgressDialog progressDialog=new ProgressDialog(HomeScreen.this);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setMessage("Logging out,Please wait...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        dialog.dismiss();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                FirebaseAuth.getInstance().signOut();
                                SharedPreferences sharedPreferences=getSharedPreferences("state",MODE_PRIVATE);
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putBoolean("isSaved",false);
                                editor.apply();
                                progressDialog.dismiss();
                                Intent intent1=new Intent(HomeScreen.this,Login.class);
                                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent1);
                            }
                        }, 800);
                    }
                });
                break;
            case R.id.delete_account:
                DeleteAccountBinding view2=DeleteAccountBinding.inflate(getLayoutInflater());
                AlertDialog dialog1=new AlertDialog.Builder(this)
                        .setCancelable(false).setView(view2.getRoot()).create();
                dialog1.getWindow().setWindowAnimations(R.style.DeleteDialogAnimation);
                dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog1.show();
                view2.deleteNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog1.dismiss();
                    }
                });
                view2.deleteYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //log out code goes here...


                        dialog1.dismiss();
                    }
                });
                break;
            case R.id.users:
                startActivity(new Intent(this,Users.class));
                break;
        }
        return true;
    }

}