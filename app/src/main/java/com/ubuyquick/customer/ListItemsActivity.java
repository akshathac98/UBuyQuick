package com.ubuyquick.customer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ubuyquick.customer.adapter.ListProductAdapter;
import com.ubuyquick.customer.adapter.ShopSelectAdapter;
import com.ubuyquick.customer.model.ListProduct;
import com.ubuyquick.customer.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListItemsActivity extends AppCompatActivity {

    private static final String TAG = "ListItemsActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private RecyclerView rv_products;
    private List<ListProduct> listProducts;
    private ListProductAdapter listProductAdapter;

    private TextView tv_head1, tv_head2, tv_head3;

    private int selected_items = 0;

    private Button btn_checkout, btn_yes, btn_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_items);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_up);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setTitle(getIntent().getStringExtra("list_name"));

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tv_head1 = (TextView) findViewById(R.id.tv_head1);
        tv_head2 = (TextView) findViewById(R.id.tv_head2);
        tv_head3 = (TextView) findViewById(R.id.tv_head3);
        rv_products = (RecyclerView) findViewById(R.id.rv_products);
        listProductAdapter = new ListProductAdapter(this, new Utils.OnItemClick() {
            @Override
            public void onClick(int count) {
                selected_items = count;
                tv_head1.setText("Buy " + selected_items + " Products");
                tv_head2.setText("Create New List With " + selected_items + " Products");
                tv_head3.setText("Add " + selected_items + " Products to Saved List");
            }
        });
        rv_products.setAdapter(listProductAdapter);
        listProducts = new ArrayList<>();

        btn_checkout = (Button) findViewById(R.id.btn_checkout);
        btn_yes = (Button) findViewById(R.id.btn_yes);
        btn_add = (Button) findViewById(R.id.btn_add);

        db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3)).collection("temp_list")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                for (DocumentSnapshot document : documents) {
                    db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                            .collection("temp_list").document(document.getId()).delete();
                }
            }
        });

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListItemsActivity.this, CreateListActivity.class);
                i.putExtra("list_id", "temp_list");
                startActivity(i);
            }
        });

        btn_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListItemsActivity.this, SelectShopActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                .collection("lists").document(getIntent().getStringExtra("list_id")).collection("products")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                for (DocumentSnapshot document : documents) {
                    Map<String, Object> product = document.getData();
                    listProducts.add(new ListProduct(product.get("product_name").toString(),
                            Double.parseDouble(product.get("product_mrp").toString()),
                            Integer.parseInt(product.get("product_quantity").toString()), document.getId(),
                            product.get("product_measure").toString()));
                }
                listProductAdapter.setListProducts(listProducts);
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
