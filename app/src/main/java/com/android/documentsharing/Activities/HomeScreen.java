package com.android.documentsharing.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.documentsharing.Adapter.ViewPagerAdapter;
import com.android.documentsharing.Fragments.Downloaded;
import com.android.documentsharing.Fragments.Received;
import com.android.documentsharing.Fragments.Shared;
import com.android.documentsharing.Holder.documentHolder;
import com.android.documentsharing.HttpTrustManager;
import com.android.documentsharing.R;
import com.android.documentsharing.databinding.DeleteAccountBinding;
import com.android.documentsharing.databinding.LogoutBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

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
        MenuItem item1=menu.findItem(R.id.delete_account);
        SpannableString string=new SpannableString("Delete Account");
        string.setSpan(new ForegroundColorSpan(Color.RED),0,string.length(),0);
        item1.setTitle(string);
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
                Shared fragment= (Shared) viewPager.getAdapter().instantiateItem(viewPager,count);
                fragment.search(text.toLowerCase());
            }else if (count == 1){
                Received fragment= (Received) viewPager.getAdapter().instantiateItem(viewPager,count);
                fragment.search(text.toLowerCase());
            }else {
                Downloaded fragment= (Downloaded) viewPager.getAdapter().instantiateItem(viewPager,count);
                fragment.search(text.toLowerCase());
            }
        }catch (Exception e){
            Log.e("Home : search ====",e.getLocalizedMessage());
        }
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
                        loadData();
                        dialog1.dismiss();
                    }
                });
                break;
            case R.id.users:
                startActivity(new Intent(this,Users.class));
                break;
            case R.id.search:

                break;
        }
        return true;
    }

    private void loadData() {
        ProgressDialog dialog=new ProgressDialog(HomeScreen.this);
        dialog.setProgressStyle(0);
        dialog.setMessage("Processing please wait...");
        dialog.setCancelable(false);
        dialog.show();
        database.child("Documents").child(auth.getCurrentUser().getUid()).child("shared").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                        documentHolder holder=snapshot1.getValue(documentHolder.class);
                        arrayList.add(holder);
                    }
                    dialog.dismiss();
                    DeleteAccount();
                }else {
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                Toast.makeText(HomeScreen.this, "Home screen : failed due to :-"+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void DeleteAccount() {
        ProgressDialog dialog=new ProgressDialog(HomeScreen.this);
        dialog.setProgressStyle(0);
        dialog.setMessage("sending OTP for verification...");
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (reference != null && listener != null){
                    reference.removeEventListener(listener);
                }
                dialogInterface.dismiss();
            }
        });
        reference=FirebaseDatabase.getInstance().getReference().child("DocumentSharing")
                .child("Document_user").child(auth.getCurrentUser().getUid())
                .child("finalNo");
        listener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String number=snapshot.getValue(String.class);
                    if (number != null){
                        HttpTrustManager.allowAllSSL();
                        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                                .setPhoneNumber(number)       // Phone number to verify
                                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                .setActivity(HomeScreen.this)                 // Activity (for callback binding)
                                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                    @Override
                                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                        Toast.makeText(HomeScreen.this, "Verified", Toast.LENGTH_SHORT).show();
                                    }
                                    @Override
                                    public void onVerificationFailed(@NonNull FirebaseException e) {
                                        dialog.dismiss();
                                        Toast.makeText(HomeScreen.this, "Login : Failed due to : -"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                        super.onCodeSent(s, forceResendingToken);
                                        dialog.dismiss();
                                        Toast.makeText(HomeScreen.this, "Code sent successfully!", Toast.LENGTH_SHORT).show();
                                        new Handler().postDelayed(() -> {
                                           View view= LayoutInflater.from(HomeScreen.this).inflate(R.layout.get_otp,null);
                                           AlertDialog dialog1=new AlertDialog.Builder(HomeScreen.this)
                                                   .setCancelable(false)
                                                   .setView(view).create();
                                           dialog1.show();
                                            Button cancel,verify;
                                            cancel=view.findViewById(R.id.Cancel);
                                            verify=view.findViewById(R.id.Verify);
                                            EditText editText=view.findViewById(R.id.OTPView);
                                            cancel.setOnClickListener(v->dialog1.dismiss());
                                            verify.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    String otp=editText.getText().toString();
                                                    if (otp.isEmpty()){
                                                        editText.setError("Enter OTP");
                                                        editText.requestFocus();
                                                    }else if (otp.length() < 6){
                                                        editText.setError("Enter valid OTP");
                                                        editText.requestFocus();
                                                    }else{
                                                        dialog1.dismiss();
                                                        ProgressDialog dialog2=new ProgressDialog(HomeScreen.this);
                                                        dialog2.setProgressStyle(0);
                                                        dialog2.setMessage("Verifying....");
                                                        dialog2.setCancelable(false);
                                                        dialog2.show();
                                                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(s,otp);
                                                        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                                if (task.isSuccessful()){
                                                                    dialog2.dismiss();
                                                                    Toast.makeText(HomeScreen.this, "verification successful!", Toast.LENGTH_SHORT).show();
                                                                    new Handler().postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            ProgressDialog dialog3=new ProgressDialog(HomeScreen.this);
                                                                            dialog3.setProgressStyle(0);
                                                                            dialog3.setMessage("Deleting Account just wait few moment....");
                                                                            dialog3.setCancelable(false);
                                                                            dialog3.show();

                                                                        }
                                                                    }, 1000);
                                                                }else {
                                                                    dialog2.dismiss();
                                                                    Toast.makeText(HomeScreen.this, "Home screen : verification not completed due to :- "+task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                dialog2.dismiss();
                                                                Toast.makeText(HomeScreen.this, "Home screen : verification failed due to :- "+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        },1000);
                                    }
                                })
                                .build();
                        PhoneAuthProvider.verifyPhoneNumber(options);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        dialog.show();
        if (reference != null && listener != null){
            reference.addListenerForSingleValueEvent(listener);
        }
    }

}