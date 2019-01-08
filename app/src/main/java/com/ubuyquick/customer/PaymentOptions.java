package com.ubuyquick.customer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentOptions extends AppCompatActivity {

    private static final String TAG = "PaymentOptions";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private RadioButton rb_cod;
    private Button btn_place_order;

    private String shop_id, shop_vendor, latitude, longitude, address, slot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_options);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_up);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        shop_id = getIntent().getStringExtra("shop_id");
        shop_vendor = getIntent().getStringExtra("shop_vendor");
        latitude = getIntent().getStringExtra("latitude");
        longitude = getIntent().getStringExtra("longitude");
        address = getIntent().getStringExtra("address");
        slot = getIntent().getStringExtra("slot");

        rb_cod = (RadioButton) findViewById(R.id.rb_cod);
        btn_place_order = (Button) findViewById(R.id.btn_place_order);

        if (getIntent().getStringExtra("checkout_list").equals("shop")) {
            db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    try {
                        final String name = task.getResult().getData().get("customer_name").toString();
                        btn_place_order.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (rb_cod.isChecked()) {

                                    db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                            .collection("shop_lists").document(shop_id).collection("lists").get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    final String timestamp = new java.sql.Timestamp(System.currentTimeMillis()).getTime() + "";
                                                    final List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                                    Map<String, Object> order = new HashMap<>();
                                                    order.put("customer_id", mAuth.getCurrentUser().getPhoneNumber().substring(3));
                                                    order.put("customer_name", name);
                                                    order.put("delivery_address", address);
                                                    order.put("latitude", latitude);
                                                    order.put("longitude", longitude);
                                                    order.put("slot", getIntent().getStringExtra("slot"));
                                                    order.put("order_id", timestamp);
                                                    order.put("count", documents.size());
                                                    order.put("ordered_at", Timestamp.now());
                                                    db.collection("vendors").document(shop_vendor).collection("shops").document(shop_id)
                                                            .collection("new_orders").document(timestamp).set(order)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    for (DocumentSnapshot document : documents) {
                                                                        Map<String, Object> product = document.getData();
                                                                        Map<String, Object> newProduct = new HashMap<>();
                                                                        newProduct.put("name", product.get("product_name").toString());
                                                                        newProduct.put("mrp", product.get("product_mrp").toString());
                                                                        newProduct.put("measure", product.get("product_measure").toString());
                                                                        newProduct.put("quantity", product.get("product_quantity").toString());
                                                                        newProduct.put("available", true);

                                                                        db.collection("vendors").document(shop_vendor).collection("shops").document(shop_id)
                                                                                .collection("new_orders").document(timestamp).collection("products")
                                                                                .document(document.getId()).set(newProduct);
                                                                    }
//                                                                setResult(Activity.RESULT_OK);
//                                                                finish();
                                                                    Intent i = new Intent(PaymentOptions.this, OrderPlacedActivity.class);
                                                                    i.putExtra("shop_id", shop_id);
                                                                    startActivity(i);
                                                                }
                                                            });

                                                    db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber()
                                                            .substring(3)).collection("orders").document(timestamp).set(order)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    for (DocumentSnapshot document : documents) {
                                                                        Map<String, Object> product = document.getData();
                                                                        Map<String, Object> newProduct = new HashMap<>();
                                                                        newProduct.put("name", product.get("product_name").toString());
                                                                        newProduct.put("measure", product.get("product_measure").toString());
                                                                        newProduct.put("mrp", product.get("product_mrp").toString());
                                                                        newProduct.put("quantity", product.get("product_quantity").toString());
                                                                        newProduct.put("available", true);

                                                                        db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber()
                                                                                .substring(3)).collection("orders").document(timestamp).collection("products")
                                                                                .document(document.getId()).set(newProduct);
                                                                    }
                                                                    setResult(Activity.RESULT_OK);
                                                                    finish();
                                                                }
                                                            });

                                                    db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                            .collection("current_list").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                                            for (DocumentSnapshot document : documents) {
                                                                db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                                        .collection("current_list").document(document.getId()).delete();
                                                            }
                                                        }
                                                    });

                                                }
                                            });

                                } else {
                                    Toast.makeText(PaymentOptions.this, "Please select a payment mode", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } catch (Exception e) {
                        Toast.makeText(PaymentOptions.this, "Please add customer name in profile section before placing orders.", Toast.LENGTH_LONG).show();
                    }


                }
            });
        } else {

            db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    final String name = task.getResult().getData().get("customer_name").toString();
                    btn_place_order.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (rb_cod.isChecked()) {

                                db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                        .collection("temp_list").get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                final String timestamp = new java.sql.Timestamp(System.currentTimeMillis()).getTime() + "";
                                                final List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                                Map<String, Object> order = new HashMap<>();
                                                order.put("customer_id", mAuth.getCurrentUser().getPhoneNumber().substring(3));
                                                order.put("customer_name", name);
                                                order.put("delivery_address", address);
                                                order.put("latitude", latitude);
                                                order.put("longitude", longitude);
                                                order.put("slot", getIntent().getStringExtra("slot"));
                                                order.put("order_id", timestamp);
                                                order.put("count", documents.size());
                                                order.put("ordered_at", Timestamp.now());
                                                db.collection("vendors").document(shop_vendor).collection("shops").document(shop_id)
                                                        .collection("new_orders").document(timestamp).set(order)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                for (DocumentSnapshot document : documents) {
                                                                    Map<String, Object> product = document.getData();
                                                                    Map<String, Object> newProduct = new HashMap<>();
                                                                    newProduct.put("name", product.get("product_name").toString());
                                                                    newProduct.put("mrp", product.get("product_mrp").toString());
                                                                    newProduct.put("measure", product.get("product_measure").toString());
                                                                    newProduct.put("quantity", product.get("product_quantity").toString());
                                                                    newProduct.put("available", true);

                                                                    db.collection("vendors").document(shop_vendor).collection("shops").document(shop_id)
                                                                            .collection("new_orders").document(timestamp).collection("products")
                                                                            .document(document.getId()).set(newProduct);
                                                                }
                                                                setResult(Activity.RESULT_OK);
                                                                finish();
                                                            }
                                                        });

                                                db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber()
                                                        .substring(3)).collection("orders").document(timestamp).set(order)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                for (DocumentSnapshot document : documents) {
                                                                    Map<String, Object> product = document.getData();
                                                                    Map<String, Object> newProduct = new HashMap<>();
                                                                    newProduct.put("name", product.get("product_name").toString());
                                                                    newProduct.put("measure", product.get("product_measure").toString());
                                                                    newProduct.put("mrp", product.get("product_mrp").toString());
                                                                    newProduct.put("quantity", product.get("product_quantity").toString());
                                                                    newProduct.put("available", true);

                                                                    db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber()
                                                                            .substring(3)).collection("orders").document(timestamp).collection("products")
                                                                            .document(document.getId()).set(newProduct);
                                                                }
                                                                setResult(Activity.RESULT_OK);
                                                                finish();
                                                            }
                                                        });

                                                db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                        .collection("current_list").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                                        for (DocumentSnapshot document : documents) {
                                                            db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                                    .collection("current_list").document(document.getId()).delete();
                                                        }
                                                    }
                                                });

                                            }
                                        });

                            } else {
                                Toast.makeText(PaymentOptions.this, "Please select a payment mode", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            });
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
