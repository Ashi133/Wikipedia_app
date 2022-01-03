package com.android.documentsharing.Activities;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.documentsharing.UpdateOnlineStatus;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class verifyNumber extends AppCompatActivity {
    private TextView label;
    private EditText otpview;
    private androidx.appcompat.widget.AppCompatButton verify;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private String Code,number,finalNo;
    private com.android.documentsharing.databinding.ActivityVerifyNumberBinding binding;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= com.android.documentsharing.databinding.ActivityVerifyNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();
        auth=FirebaseAuth.getInstance();
        label=binding.verifyLabel;
        otpview=binding.otpView;
        verify=binding.verifyNumber;
        progressBar=binding.verifyProgress;
        number=getIntent().getStringExtra("number");
        Code=getIntent().getStringExtra("code");
        finalNo=getIntent().getStringExtra("finalNo");
        label.setText("Verify "+Code+number);
        String otp1=getIntent().getStringExtra("otp");
        verify.setOnClickListener(view -> {
            if (!UpdateOnlineStatus.check_network_state(verifyNumber.this)){
                Toast.makeText(verifyNumber.this, "connection error", Toast.LENGTH_SHORT).show();
            }else{
                String code = null;
                code = Objects.requireNonNull(otpview.getText()).toString();
                if (code.isEmpty() || code.length() < 6) {
                    otpview.setError("Please enter valid OTP");
                    otpview.requestFocus();
                }else {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                    //Find the currently focused view, so we can grab the correct window token from it.
                    View view3= getCurrentFocus();
                    //If no view currently has focus, create a new one, just so we can grab a window token from it
                    if (view3 == null) {
                        view3 = new View(this);
                    }
                    imm.hideSoftInputFromWindow(view3.getWindowToken(), 0);
                    progressBar.setVisibility(View.VISIBLE);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otp1, code);
                    auth.signInWithCredential(credential)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(verifyNumber.this, finalNo+" verified successfully!", Toast.LENGTH_LONG).show();
                                    FirebaseDatabase
                                            .getInstance()
                                            .getReference()
                                            .child("document")
                                            .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                            .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnSuccessListener(unused -> new Handler().postDelayed(() -> {
                                        startActivity(new Intent(verifyNumber.this, saveProfile.class)
                                                .putExtra("number",number).putExtra("code",Code).putExtra("finalNo",finalNo)
                                                .putExtra("flag",false));
                                        finish();
                                    }, 800)).addOnFailureListener(e -> Toast.makeText(verifyNumber.this, "verify : Setting user id due to :-"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
                                } else {
                                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(verifyNumber.this, "verify : Please enter correct OTP", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(e -> {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(verifyNumber.this, "verify : Verification failed due to : -" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}