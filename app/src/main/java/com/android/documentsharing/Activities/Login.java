package com.android.documentsharing.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.documentsharing.HttpTrustManager;
import com.android.documentsharing.R;
import com.android.documentsharing.UpdateOnlineStatus;
import com.android.documentsharing.databinding.ActivityLoginBinding;
import com.android.documentsharing.isValidNumber;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {
    private CountryCodePicker picker;
    private EditText phoneNum;
    private ActivityLoginBinding binding;
    private TextView resend;
    private androidx.appcompat.widget.AppCompatButton button,sign_in;
    private ProgressBar progressBar;
    private String countryCode=null;
    //private String patterns="(0/91)?[7-9][0-9]{9}";
    private FirebaseAuth auth;
    private String finalNumber;
    private PhoneAuthOptions options;
    private static final Pattern password_pattern =
            Pattern.compile("^" +
                    "(?=.*[@#$%^&+=.])" +     // at least 1 special character
                    "(?=\\S+$)" +            // no white spaces
                    ".{4,}" +                // at least 4 characters
                    "$");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();
        auth=FirebaseAuth.getInstance();
        picker=binding.countryCodePicker;
        phoneNum=binding.phoneNumber;
        button=binding.continueVerify;
        progressBar= binding.loginProgress;
        resend= binding.resendOtp;
        picker.setAutoDetectedCountry(true);
        countryCode=picker.getSelectedCountryCodeWithPlus();
        button.setClickable(false);
        picker.setOnCountryChangeListener(() -> countryCode=picker.getSelectedCountryCodeWithPlus());
        phoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String number=phoneNum.getText().toString();
                if (number.isEmpty()){
                    phoneNum.setError("Please enter number");
                    phoneNum.requestFocus();
                    button.setClickable(false);
                }
                else if (!isValidNumber.isValid(number)){
                    phoneNum.setError("Invalid number");
                    phoneNum.requestFocus();
                    button.setClickable(false);
                }
                else {
                    button.setClickable(true);
                    countryCode=picker.getSelectedCountryCodeWithPlus();
                    finalNumber=countryCode+number;
                    button.setClickable(true);
                    Log.e("number=",finalNumber);
                    HttpTrustManager.allowAllSSL();
                    options = PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber(finalNumber)       // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(Login.this)                 // Activity (for callback binding)
                            .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                    Toast.makeText(Login.this, "Verified", Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(Login.this, "Login : Failed due to : -"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    resend.setVisibility(View.VISIBLE);
                                    button.setClickable(true);
                                }

                                @Override
                                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    super.onCodeSent(s, forceResendingToken);
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(Login.this, "Code sent successfully!", Toast.LENGTH_SHORT).show();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(
                                                    new Intent(Login.this,verifyNumber.class)
                                                            .putExtra("number",number)
                                                            .putExtra("code",countryCode)
                                                            .putExtra("finalNo",finalNumber)
                                                            .putExtra("otp",s)
                                            );
                                            finish();
                                        }
                                    },800);
                                }
                            })
                            .build();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        button.setOnClickListener(view -> {
            if (!UpdateOnlineStatus.check_network_state(Login.this)){
                Toast.makeText(Login.this, "connection error", Toast.LENGTH_SHORT).show();
            }else if (phoneNum.getText().toString().isEmpty()){
                phoneNum.setError("Please enter number");
                phoneNum.requestFocus();
            }
            else {
                InputMethodManager imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                //Find the currently focused view, so we can grab the correct window token from it.
                View view3= getCurrentFocus();
                //If no view currently has focus, create a new one, just so we can grab a window token from it
                if (view3 == null) {
                    view3 = new View(this);
                }
                imm.hideSoftInputFromWindow(view3.getWindowToken(), 0);
                progressBar.setVisibility(View.VISIBLE);
                PhoneAuthProvider.verifyPhoneNumber(options);
                button.setClickable(false);
            }
        });
        resend.setOnClickListener(view -> {
            if (!UpdateOnlineStatus.check_network_state(Login.this)){
                Toast.makeText(Login.this, "connection error", Toast.LENGTH_SHORT).show();
            }else {
                progressBar.setVisibility(View.VISIBLE);
                PhoneAuthProvider.verifyPhoneNumber(options);
                resend.setVisibility(View.GONE);
            }
        });
    }
}