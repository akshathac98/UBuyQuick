package com.ubuyquick.customer;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ubuyquick.customer.adapter.ShopAdapter;
import com.ubuyquick.customer.model.Shop;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShopsActivity extends AppCompatActivity {

    private static final String TAG = "ShopsActivity";

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private RecyclerView rv_shops;
    private ShopAdapter shopAdapter;
    private List<Shop> shops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_up);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rv_shops = findViewById(R.id.rv_shops);
        shopAdapter = new ShopAdapter(this);
        shops = new ArrayList<>();
        rv_shops.setAdapter(shopAdapter);

        db = FirebaseFirestore.getInstance();
        Query query = db.collection("shops_index").whereEqualTo("pincode", "560091");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    for (DocumentSnapshot document : documents){
                        Map<String, Object> shop = document.getData();
                        shops.add(new Shop(Boolean.parseBoolean(shop.get("quick_delivery").toString()),
                                shop.get("pincode").toString(), document.getId(), shop.get("shop_name").toString(),
                                shop.get("timings").toString(), shop.get("image_url").toString(), shop.get("address").toString(),
                                shop.get("vendor").toString(), shop.get("shop_specialization").toString(),
                                Double.parseDouble(shop.get("minimum_order").toString())));
                        shopAdapter.setShops(shops);
                    }
                } else {
                    Toast.makeText(ShopsActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();

                }
            }
        });
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
