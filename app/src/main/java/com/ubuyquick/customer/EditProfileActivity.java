package com.ubuyquick.customer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextInputEditText et_name, et_contact, et_email;
    private TextInputLayout til_name, til_contact, til_email;
    private Button btn_save;

    private static final String EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
    private static Pattern pattern;
    private Matcher matcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        et_name = (TextInputEditText) findViewById(R.id.et_name);
        et_contact = (TextInputEditText) findViewById(R.id.et_contact);
        et_email = (TextInputEditText) findViewById(R.id.et_email);

        til_name = (TextInputLayout) findViewById(R.id.til_name);
        til_contact = (TextInputLayout) findViewById(R.id.til_contact);
        til_email = (TextInputLayout) findViewById(R.id.til_email);
        btn_save = (Button) findViewById(R.id.btn_save);

        Intent i = getIntent();
        et_contact.setText(i.getStringExtra("contact"));
        et_name.setText(i.getStringExtra("name"));
        et_email.setText(i.getStringExtra("email"));

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                til_contact.setErrorEnabled(false);
                til_name.setErrorEnabled(false);
                til_email.setErrorEnabled(false);

                if (validateForm()) {
                    Map<String, Object> info = new HashMap<>();
                    info.put("customer_name", et_name.getText().toString());
                    info.put("customer_email", et_email.getText().toString());

                    db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                            .update(info)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Intent i = new Intent();
                                    i.putExtra("name", et_name.getText().toString());
                                    i.putExtra("email", et_email.getText().toString());
                                    setResult(RESULT_OK, i);
                                    finish();
                                }
                            });
                }
            }
        });
    }

    private boolean validateForm() {
        String name = et_name.getText().toString();
        String contact = et_contact.getText().toString();
        String email = et_email.getText().toString();

        if (TextUtils.isEmpty(name)) {
            til_name.setError("Name cannot be empty");
            et_name.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(email)) {
            til_email.setError("Email cannot be empty");
            et_email.requestFocus();
            return false;
        }

        pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            til_email.setError("Invalid email address format.");
            return false;
        }

        return true;
    }
}
