package com.ubuyquick.customer.auth;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ubuyquick.customer.MainActivity;
import com.ubuyquick.customer.R;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VerifyOTPActivity extends AppCompatActivity {

    private static final String TAG = "VerifyOTPActivity";

    private String mobile_number, mVerificationId, verification_type, email, address, pincode, name;

    private TextView tv_code;
    private Button btn_verify;
    private PinView pinView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private PhoneAuthProvider.ForceResendingToken token;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        mobile_number = getIntent().getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        name = getIntent().getStringExtra("name");
        address = getIntent().getStringExtra("address");
        pincode = getIntent().getStringExtra("pincode");
        email = getIntent().getStringExtra("email");
        verification_type = getIntent().getStringExtra("VERIFICATION_TYPE");

        tv_code = (TextView) findViewById(R.id.tv_code);
        btn_verify = (Button) findViewById(R.id.btn_verify);
        pinView = (PinView) findViewById(R.id.pinView);

        tv_code.setText("Verification code sent to " + mobile_number);

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, pinView.getText().toString());
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "onComplete: sign in success");
                                    final Intent i = new Intent(VerifyOTPActivity.this, MainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    if (verification_type.equals("REGISTER") || task.getResult().getAdditionalUserInfo().isNewUser()) {
                                        Map<String, Object> user = new HashMap<>();
                                        user.put("customer_email", email);
                                        user.put("customer_name", name);
                                        user.put("delivery_address", address);
                                        user.put("customer_id", task.getResult().getUser().getUid());
                                        user.put("customer_phone", mobile_number);
                                        user.put("photo_url", "NA");

                                        Map<String, Object> userRole = new HashMap<>();
                                        user.put("user_role", "CUSTOMER");
                                        user.put("user_id", mobile_number);

                                        db.collection("users").document(mobile_number).set(userRole);

                                        db.collection("customers")
                                                .document(mobile_number).set(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        startActivity(i);
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(VerifyOTPActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        startActivity(i);
                                        finish();
                                    }
                                } else {
                                    Log.d(TAG, "onComplete: failure: " + task.getException());
                                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                        Toast.makeText(VerifyOTPActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: " + e.getLocalizedMessage());
                                Toast.makeText(VerifyOTPActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                mVerificationId = s;
                token = forceResendingToken;
                Toast.makeText(VerifyOTPActivity.this, "OTP verification code sent", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onCodeSent: " + s);
            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted: " + phoneAuthCredential.getSmsCode());
                try {
                    pinView.setText(phoneAuthCredential.getSmsCode());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                btn_verify.setOnClickListener(null);

                mAuth.signInWithCredential(phoneAuthCredential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "onComplete: sign in success");
                                    final Intent i = new Intent(VerifyOTPActivity.this, MainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    if (verification_type.equals("REGISTER") || task.getResult().getAdditionalUserInfo().isNewUser()) {
                                        Map<String, Object> user = new HashMap<>();
                                        user.put("customer_email", email);
                                        user.put("customer_name", name);
                                        user.put("customer_location", "NA");
                                        user.put("customer_id", task.getResult().getUser().getUid());
                                        user.put("customer_phone", mobile_number);
                                        user.put("photo_url", "NA");

                                        Map<String, Object> userRole = new HashMap<>();
                                        user.put("user_role", "CUSTOMER");
                                        user.put("user_id", mobile_number);

                                        db.collection("users").document(mobile_number).set(userRole);

                                        db.collection("customers")
                                                .document(mobile_number).set(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        startActivity(i);
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(VerifyOTPActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        startActivity(i);
                                        finish();
                                    }
                                } else {
                                    Log.d(TAG, "onComplete: failure: " + task.getException());
                                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                        Toast.makeText(VerifyOTPActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: " + e.getLocalizedMessage());
                                Toast.makeText(VerifyOTPActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                Toast.makeText(VerifyOTPActivity.this, "Verified successfully.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.d(TAG, "onVerificationFailed: " + e.getLocalizedMessage());
                Toast.makeText(VerifyOTPActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + mobile_number,
                60, TimeUnit.SECONDS, VerifyOTPActivity.this, mCallbacks);
    }
}