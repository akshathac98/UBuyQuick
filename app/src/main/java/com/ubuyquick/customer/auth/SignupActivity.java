package com.ubuyquick.customer.auth;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ubuyquick.customer.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private static final String EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
    private static Pattern pattern;
    private Matcher matcher;

    private Button btn_cancel, btn_signup;
    private TextInputLayout til_user_name, til_email, til_mobile_number, til_address, til_pincode;
    private TextInputEditText et_user_name, et_email, et_mobile_number, et_address, et_pincode;
    String user_name, email, mobile_number, address, pincode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        til_user_name = (TextInputLayout) findViewById(R.id.til_user_name);
        til_email = (TextInputLayout) findViewById(R.id.til_email);
        til_mobile_number = (TextInputLayout) findViewById(R.id.til_mobile_number);

        et_user_name = (TextInputEditText) findViewById(R.id.et_user_name);
        et_email = (TextInputEditText) findViewById(R.id.et_email);
        et_mobile_number = (TextInputEditText) findViewById(R.id.et_mobile_number);

        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_signup = (Button) findViewById(R.id.btn_signup);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                til_user_name.setErrorEnabled(false);
                til_mobile_number.setErrorEnabled(false);
                til_email.setErrorEnabled(false);

                if (validateForm()) {
                    Intent i = new Intent(SignupActivity.this, VerifyOTPActivity.class);
                    i.putExtra("name", user_name);
                    i.putExtra("email", email);
                    i.putExtra(Intent.EXTRA_PHONE_NUMBER, mobile_number);
                    i.putExtra("VERIFICATION_TYPE", "REGISTER");
                    startActivity(i);
                }

            }
        });
    }

    private boolean validateForm() {
        user_name = et_user_name.getText().toString();
        email = et_email.getText().toString();
        mobile_number = et_mobile_number.getText().toString();

        if (TextUtils.isEmpty(user_name)) {
            til_user_name.setError("Name cannot be empty.");
            et_user_name.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(mobile_number)) {
            til_mobile_number.setError("Mobile number required.");
            et_mobile_number.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(email)) {
            til_email.setError("Email required.");
            et_email.requestFocus();
            return false;
        }

        if (mobile_number.length() != 10) {
            til_mobile_number.setError("Mobile number should have 10 digits.");
            return false;
        }

        if (!email.equals("NA")) {
            pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(email);
            if (!matcher.matches()) {
                til_email.setError("Invalid email address format.");
                return false;
            }
        }

        return true;
    }
}
