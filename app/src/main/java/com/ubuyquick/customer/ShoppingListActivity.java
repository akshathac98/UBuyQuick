package com.ubuyquick.customer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ubuyquick.customer.adapter.AdAdapter;
import com.ubuyquick.customer.model.Ad;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListActivity extends AppCompatActivity {

    private static final String TAG = "ShoppingListActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private boolean doubleBackToExitPressedOnce = false;
    private ArrayAdapter<String> arrayAdapter;

    private List<Ad> ads;
    private AdAdapter adAdapter;

    private LinearLayout btn_shops, btn_profile, btn_shoppinglist;
    private Button btn_create, btn_searchlist;
    private RecyclerView rv_ads;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeViews();
        initialize();
    }

    private void initializeViews() {

        btn_profile = (LinearLayout) findViewById(R.id.layout3);
        btn_shoppinglist = (LinearLayout) findViewById(R.id.layout2);
        btn_shops = (LinearLayout) findViewById(R.id.layout1);
        btn_create = (Button) findViewById(R.id.btn_create);
        btn_searchlist = (Button) findViewById(R.id.btn_searchlist);

        rv_ads = (RecyclerView) findViewById(R.id.rv_ads);

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShoppingListActivity.this, CreateListActivity.class));
            }
        });

        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShoppingListActivity.this, ProfileActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        btn_searchlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShoppingListActivity.this, LoadListActivity.class));
            }
        });

        btn_shops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShoppingListActivity.this, MainActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });
    }

    private void initialize() {

        ads = new ArrayList<>();
        ads.add(new Ad(R.mipmap.haldiram_ad));
        ads.add(new Ad(R.mipmap.kellogs_ad));
        ads.add(new Ad(R.mipmap.nandhini_ad));
        ads.add(new Ad(R.mipmap.oil_ad));

        adAdapter = new AdAdapter(ShoppingListActivity.this, ads);
        rv_ads.setAdapter(adAdapter);
        adAdapter.setAds(ads);
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
}
