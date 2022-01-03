package com.android.documentsharing.Activities;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.documentsharing.Holder.Users;
import com.android.documentsharing.R;
import com.android.documentsharing.UpdateOnlineStatus;
import com.android.documentsharing.databinding.ActivitySaveProfileBinding;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
@SuppressWarnings("ALL")
public class saveProfile extends AppCompatActivity {
    private CircleImageView profile;
    private EditText name,about;
    private TextView label,label1;
    private androidx.appcompat.widget.AppCompatButton saveBtn;
    private ProgressBar progressBar;
    private Uri uri=null;
    private String userName,imgPath=null,userAbout;
    private StorageReference reference;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private String number,code,finalNo;
    private ActivitySaveProfileBinding binding;
    boolean from=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySaveProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();
        reference= FirebaseStorage.getInstance().getReference().child("document");
        databaseReference= FirebaseDatabase.getInstance().getReference().child("DocumentSharing").child("Document_user");
        auth=FirebaseAuth.getInstance();
        profile=binding.profilePic3;
        name=binding.userName3;
        about=binding.userAbout3;
        label= binding.nameSizeCount3;
        label1= binding.aboutSizeCount3;
        saveBtn= binding.saveProfile3;
        progressBar= binding.profileProgress3;
        number=getIntent().getStringExtra("number");
        code=getIntent().getStringExtra("code");
        finalNo=getIntent().getStringExtra("finalNo");
        from=getIntent().getBooleanExtra("from",false);
        if (from){
            binding.linear4.setVisibility(View.VISIBLE);
            if (UpdateOnlineStatus.check_network_state(saveProfile.this)){
                databaseReference.child(auth.getCurrentUser().getUid()).child("private").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            boolean value=snapshot.getValue(boolean.class);
                            binding.switch1.setChecked(value);
                        }else {
                            binding.switch1.setChecked(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }else{
                binding.switch1.setChecked(false);
            }
        }else {
            binding.linear4.setVisibility(View.GONE);
        }
        binding.switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!UpdateOnlineStatus.check_network_state(saveProfile.this)){
                    Toast.makeText(saveProfile.this, "connection error!", Toast.LENGTH_SHORT).show();
                }else{
                    databaseReference.child(auth.getCurrentUser().getUid()).child("private").setValue(binding.switch1.isChecked());
                }
            }
        });
        Show_previous_data();
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                userName=name.getText().toString();
                if (userName.length() > 0){
                    label.setText(String.valueOf(25-userName.length()));
                }
                else {
                    label.setText(null);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        about.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                userAbout=about.getText().toString();
                if (userAbout.length() > 0){
                    label1.setText(String.valueOf(30-userAbout.length()));
                }
                else {
                    label1.setText(null);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        profile.setOnClickListener(view -> {
            Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent,21);
        });
        saveBtn.setOnClickListener(view -> {
            userName=name.getText().toString();
            userAbout=about.getText().toString();
            if (userName.length() <= 0){
                name.setError("Please enter your name");
                name.requestFocus();
            }else if (userAbout.length() <= 0){
                about.setError("write something about you");
                about.requestFocus();
            }
            else{
                saveMyData();
            }
        });
    }

    private void saveMyData() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view1 = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view1 == null) {
            view1 = new View(this);
        }
        imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        if (!UpdateOnlineStatus.check_network_state(saveProfile.this)){
            Toast.makeText(saveProfile.this, "connection error", Toast.LENGTH_SHORT).show();
        }else {
            progressBar.setVisibility(View.VISIBLE);
            Users users;
            users = new Users(userName,userAbout, Objects.requireNonNull(auth.getCurrentUser()).getUid(),code,number,finalNo,imgPath);
            users.setPrivate(binding.switch1.isChecked());
            if (uri == null) {
                sendDataToDatabase(users);
            } else {
                sendDataToStorage(uri);
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==21){
            if (resultCode == RESULT_OK) {
                assert data != null;
                Log.e("Uri path=",data.getData().toString());
                if (data.getData() != null) {
                    uri=data.getData();
                    profile.setImageURI(uri);
                    imgPath=uri.toString();
                }
            }
        }
    }
    private void sendDataToDatabase(Users users) {
        databaseReference.child(Objects.requireNonNull(auth.getCurrentUser()).getUid()).setValue(users)
                .addOnSuccessListener(unused -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(saveProfile.this, users.getName()+" Profile Updated!", Toast.LENGTH_LONG).show();
                    SharedPreferences sharedPreferences=getSharedPreferences("state",MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putBoolean("isSaved",true);
                    editor.apply();
                    new Handler().postDelayed(() -> {
                        if (!from){
                            startActivity(new Intent(saveProfile.this,HomeScreen.class));
                        }
                        finish();
                    },800);
                }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(saveProfile.this, "save profile : Failed to save data due to :-"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        });
    }
    private void sendDataToStorage(Uri users) {
        Bitmap bitmap=null;
        try {
            bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),users);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream byteArray=new ByteArrayOutputStream();
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG,75,byteArray);
        }
        byte[] bytes=byteArray.toByteArray();
        String filename= "image.jpg";
        StorageReference reference1=reference
                .child(Objects.requireNonNull(auth.getCurrentUser())
                        .getUid())
                .child(filename);
        UploadTask uploadTask=reference1.putBytes(bytes);
        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                reference1.getDownloadUrl().addOnSuccessListener(uri1 -> {
                    imgPath=uri1.toString();
                    userName=name.getText().toString();
                    userAbout=about.getText().toString();
                    Users users1;
                    users1=new Users(userName,userAbout, Objects.requireNonNull(auth.getCurrentUser()).getUid(),code,number,finalNo,imgPath);
                    users1.setPrivate(binding.switch1.isChecked());
                    sendDataToDatabase(users1);
                }).addOnFailureListener(e -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(saveProfile.this, "save profile : Failed to get url due to:-"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(saveProfile.this, "save profile : Failed to upload profile due to:-"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        });
    }
    private void Show_previous_data() {
        if (UpdateOnlineStatus.check_network_state(saveProfile.this)){
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                databaseReference.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            Users user_holder = snapshot.getValue(Users.class);
                            if (user_holder != null) {
                                if (user_holder.getName() != null) {
                                    userName = user_holder.getName();
                                    name.setText(userName);
                                }
                                if (user_holder.getAbout() != null) {
                                    userAbout = user_holder.getAbout();
                                    about.setText(userAbout);
                                }
                                if (user_holder.getUrl() != null) {
                                    imgPath = user_holder.getUrl();
                                    try {
                                        Glide.with(saveProfile.this).load(Uri.parse(imgPath)).centerCrop().placeholder(R.drawable.user).into(profile);
                                    } catch (Exception e) {
                                        Log.e("Error = ", e.getLocalizedMessage());
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
        else {
            Toast.makeText(this, "Please check your internet connection and try again!", Toast.LENGTH_SHORT).show();
        }
    }
}