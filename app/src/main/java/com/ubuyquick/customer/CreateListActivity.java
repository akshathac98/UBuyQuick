package com.ubuyquick.customer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ubuyquick.customer.adapter.ListProductAdapter;
import com.ubuyquick.customer.adapter.MainSearchAdapter;
import com.ubuyquick.customer.adapter.NewListProductAdapter;
import com.ubuyquick.customer.model.ListProduct;
import com.ubuyquick.customer.model.MainSearchProduct;
import com.ubuyquick.customer.model.NewListProduct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Credentials;

public class CreateListActivity extends AppCompatActivity {

    private static final String TAG = "CreateListActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private RelativeLayout btn_add_product;
    private RecyclerView rv_list;
    private EditText et_name;
    private AutoCompleteTextView et_typing;
    private ImageButton btn_plus;
    private Button btn_save;
    private List<NewListProduct> newListProducts;
    private NewListProductAdapter newListProductAdapter;

    private MainSearchAdapter searchAdapter;
    private List<MainSearchProduct> searchProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_list);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_up);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3)).collection("current_list")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                for (DocumentSnapshot document : documents) {
                    db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                            .collection("current_list").document(document.getId()).delete();
                }
            }
        });

        initializeViews();
        initialize();
    }

    private void initializeViews() {
        rv_list = (RecyclerView) findViewById(R.id.rv_list);
        newListProductAdapter = new NewListProductAdapter(CreateListActivity.this);
        rv_list.setAdapter(newListProductAdapter);
        newListProducts = new ArrayList<>();

        btn_plus = (ImageButton) findViewById(R.id.btn_plus);
        btn_save = (Button) findViewById(R.id.btn_save);
        et_typing = (AutoCompleteTextView) findViewById(R.id.et_typing);
//        et_name = (EditText) findViewById(R.id.et_list_name);

        /**
         * Set up search
         */

        et_typing.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final Map<String, Double> priceList = new HashMap<>();
                int layoutItemId = android.R.layout.simple_dropdown_item_1line;
                final List<String> prodList = new ArrayList<>();
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(CreateListActivity.this, layoutItemId, prodList);

                final List<MainSearchProduct> searchProducts = new ArrayList<>();
                searchAdapter = new MainSearchAdapter(CreateListActivity.this, 1, searchProducts,
                        newListProductAdapter, newListProducts, et_typing);

                et_typing.setAdapter(searchAdapter);

                /*
                et_typing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final String selected_product = et_typing.getText().toString();
                        et_typing.setText("");
                        int pos = prodList.indexOf(selected_product);

                        final Map<String, Object> product = new HashMap<>();
                        product.put("product_name", selected_product);
                        product.put("product_quantity", 1);
                        product.put("product_mrp", priceList.get(selected_product));
                        db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3)).collection("current_list")
                                .add(product).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                product.put("product_id", task.getResult().getId());
                                searchProducts.add(new MainSearchProduct(selected_product, Double.parseDouble(product.get("product_mrp").toString())
                                , "Measure", 1));
                                searchAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
*/

                HashMap<String, String> headerMap = new HashMap<>();
                headerMap.put("Authorization", Credentials.basic("elastic", "IcORsWAWIOYtaZLNpJgbUvw1"));

                HashMap<String, String> queryMap = new HashMap<>();
                queryMap.put("q", "Products:*" + s.toString() + "*");
//                queryMap.put("from", "0");
//                queryMap.put("size", "6");

                AndroidNetworking.get("https://08465455b9e04080ada3e4855fc4fc86.ap-southeast-1.aws.found.io:9243/ubq-has/_search")
                        .addQueryParameter(queryMap)
                        .addHeaders(headerMap)
                        .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray productsArray = response.getJSONObject("hits").getJSONArray("hits");
                            for (int i = 0; i < productsArray.length(); i++) {
                                JSONObject productObj = productsArray.getJSONObject(i);
                                String product_name = productObj.getJSONObject("_source").getString("Products");
                                String product_measure = productObj.getJSONObject("_source").getString("Measure");
                                String image_url = productObj.getJSONObject("_source").getString("Url");
                                double product_mrp = productObj.getJSONObject("_source").getDouble("Price");

                                priceList.put(product_name, productObj.getJSONObject("_source").getDouble("Price"));
                                searchProducts.add(new MainSearchProduct(product_name, product_mrp, product_measure, 1, image_url));
                            }
                            searchAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Log.d(TAG, "onResponse: exception: " + e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: " + anError.getErrorDetail());
                    }
                });

            }
        });

        if (getIntent().getStringExtra("list_id") != null) {
            db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                    .collection("temp_list").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    for (DocumentSnapshot document : documents) {
                        Map<String, Object> product = document.getData();
                        newListProducts.add(new NewListProduct(product.get("product_name").toString(),
                                Integer.parseInt(product.get("product_quantity").toString()),
                                document.getId(), Double.parseDouble(product.get("product_mrp").toString()),
                                product.get("product_measure").toString()));
                    }
                    newListProductAdapter.setNewListProducts(newListProducts);
                }
            });
        }

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(CreateListActivity.this);

                View viewInflated = LayoutInflater.from(CreateListActivity.this).inflate(R.layout.dialog_list_save, null, false);
                final TextInputEditText et_name = (TextInputEditText) viewInflated.findViewById(R.id.et_name);

                builder.setView(viewInflated);
                builder.setMessage("Enter List Name")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (et_name.getText().toString().isEmpty()) {
                                    Toast.makeText(CreateListActivity.this, "Please enter the list name", Toast.LENGTH_SHORT).show();
                                } else {
                                    Map<String, Object> list = new HashMap<>();
                                    list.put("list_name", et_name.getText().toString());
                                    list.put("list_count", newListProducts.size());
                                    list.put("created_at", com.google.firebase.Timestamp.now());
                                    db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                            .collection("lists").document(et_name.getText().toString()).set(list).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            for (NewListProduct newListProduct : newListProducts) {
                                                Map<String, Object> product = new HashMap<>();
                                                product.put("product_name", newListProduct.getProductName());
                                                product.put("product_mrp", newListProduct.getProductMrp());
                                                product.put("product_measure", newListProduct.getProductMeasure());
                                                product.put("product_quantity", newListProduct.getProductQuantity());
                                                db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                        .collection("lists").document(et_name.getText().toString())
                                                        .collection("products").add(product);

                                            }
                                            Toast.makeText(CreateListActivity.this, "List saved successfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    });

                                }
                            }
                        });
                builder.show();

            }
        });


        btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_typing.getText().toString().length() > 0) {
                    final Map<String, Object> product = new HashMap<>();
                    product.put("product_name", et_typing.getText().toString());
                    product.put("product_quantity", 1);
                    product.put("product_measure", " ");
                    product.put("product_mrp", 0.0);
                    db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3)).collection("current_list")
                            .add(product).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            product.put("product_id", task.getResult().getId());
                            newListProducts.add(new NewListProduct(et_typing.getText().toString(), 1, product.get("product_id").toString(), 0.0,
                                    " "));
                            newListProductAdapter.setNewListProducts(newListProducts);
                        }
                    });
                }
            }
        });
    }

    private void initialize() {
        /*
        db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3)).collection("current_list")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                for (DocumentSnapshot document : documents) {
                    Map<String, Object> product = document.getData();
                    newListProducts.add(new NewListProduct(product.get("product_name").toString(), Integer.parseInt(product.get("product_quantity").toString()), document.getId()));
                }
                newListProductAdapter.setNewListProducts(newListProducts);
            }
        });
        */
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
