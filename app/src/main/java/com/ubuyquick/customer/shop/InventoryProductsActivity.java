package com.ubuyquick.customer.shop;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ubuyquick.customer.R;
import com.ubuyquick.customer.adapter.InventoryProductAdapter;
import com.ubuyquick.customer.model.MainSearchProduct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Credentials;

public class InventoryProductsActivity extends AppCompatActivity {

    private static final String TAG = "InventoryProductsActivi";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private RecyclerView rv_products;
    private List<MainSearchProduct> products;
    private InventoryProductAdapter productAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_products);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        rv_products = (RecyclerView) findViewById(R.id.rv_products);
        products = new ArrayList<>();
        productAdapter = new InventoryProductAdapter(this, getIntent().getStringExtra("shop_id"));
        rv_products.setAdapter(productAdapter);

        final HashMap<String, Double> priceList = new HashMap<>();

        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", Credentials.basic("elastic", "k0TWsTm4bb59v5JmnbBni27N"));

        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.put("q", "Sscat:\"" + getIntent().getStringExtra("category") + "\"");
        queryMap.put("from", "0");
        queryMap.put("size", "200");

        AndroidNetworking.get("https://8ec7da3e09b84f9fabf3785d0ae0cc40.europe-west1.gcp.cloud.es.io:9243/ubq-has/_search")
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
                        products.add(new MainSearchProduct(product_name, product_mrp, product_measure, 1, image_url));
                    }
                    productAdapter.setProducts(products);
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
}
