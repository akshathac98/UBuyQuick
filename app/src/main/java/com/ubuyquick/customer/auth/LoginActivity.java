package com.ubuyquick.customer.auth;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.ubuyquick.customer.MainActivity;
import com.ubuyquick.customer.R;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private String terms = "New to UBuyQuick? Click here to <a><font color='#fb913a'>Sign Up</font></a>";

    private TextView tv_terms;
    private TextInputEditText et_mobile_number;
    private Button btn_login, btn_signup;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialize();
        initializeViews();
        initializeListeners();
    }

    private void initialize() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            Log.d(TAG, "initialize: user: " + mUser.getPhoneNumber());
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
    }

    private void initializeViews() {
        tv_terms = (TextView) findViewById(R.id.btn_signup);
        tv_terms.setText(Html.fromHtml(terms));
        et_mobile_number = (TextInputEditText) findViewById(R.id.et_mobile_number);
        btn_login = (Button) findViewById(R.id.btn_login);
    }

    private void initializeListeners() {

        tv_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                    i.putExtra("VERIFICATION_TYPE", "REGISTER");
                    startActivity(i);

            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobile_number = et_mobile_number.getText().toString();
                if (mobile_number.length() == 10) {
                    Intent i = new Intent(LoginActivity.this, VerifyOTPActivity.class);
                    i.putExtra("VERIFICATION_TYPE", "LOGIN");
                    i.putExtra(Intent.EXTRA_PHONE_NUMBER, mobile_number);
                    startActivity(i);
                } else {
                    Toast.makeText(LoginActivity.this, "Enter valid mobile number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}