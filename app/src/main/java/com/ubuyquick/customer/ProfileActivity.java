package com.ubuyquick.customer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ubuyquick.customer.auth.LoginActivity;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private boolean doubleBackToExitPressedOnce = false;
    private ListView lv_options;

    private LinearLayout btn_shops, btn_profile, btn_shoppinglist;
    private TextView tv_username, tv_mobile, tv_email;
    private ImageButton btn_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        lv_options = (ListView) findViewById(R.id.lv_options);
        lv_options.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 2) {
                    startActivity(new Intent(ProfileActivity.this, CreditsActivity.class));
                } else if (position == 3) {
                    startActivity(new Intent(ProfileActivity.this, AddressesActivity.class));
                } else if (position == 5) {
                    View viewInflated = LayoutInflater.from(ProfileActivity.this).inflate(R.layout.dialog_referral, null, false);

                    final TextInputEditText email = (TextInputEditText) viewInflated.findViewById(R.id.et_email);

                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle("Referral email:");
                    builder.setView(viewInflated);
                    builder.setNegativeButton("Cancel", null);
                    builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Map<String, Object> referral = new HashMap<>();
                            if (!TextUtils.isEmpty(email.getText().toString())) {
                                referral.put("email", email.getText().toString());
                                referral.put("referrer", mAuth.getCurrentUser().getPhoneNumber().substring(3));
                                db.collection("referrals").add(referral);
                                Toast.makeText(ProfileActivity.this, "Referral submitted. Thank You.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.show();
                } else if (position == 6) {
                    startActivity(new Intent(ProfileActivity.this, ContactUs.class));
                } else if (position == 8) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setMessage("Confirm Log Out?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                                    finish();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();
                }
            }
        });
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.header_options, lv_options, false);
        tv_email = header.findViewById(R.id.tv_email);
        tv_username = header.findViewById(R.id.tv_username);
        tv_mobile = header.findViewById(R.id.tv_mobile);
        btn_edit = header.findViewById(R.id.btn_edit);
        lv_options.addHeaderView(header);

        btn_profile = (LinearLayout) findViewById(R.id.layout3);
        btn_shoppinglist = (LinearLayout) findViewById(R.id.layout2);
        btn_shops = (LinearLayout) findViewById(R.id.layout1);

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, EditProfileActivity.class);
                i.putExtra("name", tv_username.getText().toString());
                i.putExtra("contact", tv_mobile.getText().toString());
                i.putExtra("email", tv_email.getText().toString());
                startActivityForResult(i, 1);
            }
        });

        btn_shoppinglist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, ShoppingListActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

        btn_shops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

        db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Map<String, Object> profile = task.getResult().getData();
                try {
                    tv_username.setText(profile.get("customer_name").toString());
                } catch (Exception e) {
                    tv_username.setText("---");
                }
                try {
                    tv_email.setText(profile.get("customer_email").toString());
                } catch (Exception e) {
                    tv_email.setText("---");
                }
                tv_mobile.setText(mAuth.getCurrentUser().getPhoneNumber().substring(3));
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to exit UBuyQuick", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            tv_email.setText(data.getStringExtra("email"));
            tv_username.setText(data.getStringExtra("name"));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            super.onBackPressed();
        }
        return false;
    }

}
