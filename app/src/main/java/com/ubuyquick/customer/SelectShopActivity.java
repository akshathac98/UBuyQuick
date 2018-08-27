package com.ubuyquick.customer;

import android.content.Context;
import android.location.Address;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ubuyquick.customer.adapter.ShopAdapter;
import com.ubuyquick.customer.adapter.ShopSelectAdapter;
import com.ubuyquick.customer.model.Shop;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SelectShopActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "SelectShopActivity";

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private boolean doubleBackToExitPressedOnce = false;

    private static final int REQUEST_LOCATION = 1;
    private Double latitude, longitude;

    private RecyclerView rv_shops;
    private ShopSelectAdapter shopAdapter;
    private List<Shop> shops;
    private List<Address> addresses;
    private EditText et_search;
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_shop);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        refreshLayout.setOnRefreshListener(this);
        rv_shops = findViewById(R.id.rv_shops);
        shopAdapter = new ShopSelectAdapter(this);
        shops = new ArrayList<>();
        rv_shops.setAdapter(shopAdapter);

        et_search = (EditText) findViewById(R.id.et_search);

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                creditAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        db = FirebaseFirestore.getInstance();

        loadShops();
    }

    @Override
    public void onRefresh() {
        loadShops();
//        refreshLayout.setRefreshing(false);
        /*shops.clear();
        shopAdapter.notifyDataSetChanged();
        Query query = db.collection("shops_index").whereEqualTo("pincode", "560091");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    for (DocumentSnapshot document : documents) {
                        Map<String, Object> shop = document.getData();
                        shops.add(new Shop(Boolean.parseBoolean(shop.get("quick_delivery").toString()),
                                shop.get("pincode").toString(), document.getId(), shop.get("shop_name").toString(),
                                shop.get("timings").toString(), shop.get("image_url").toString(), shop.get("address").toString(),
                                shop.get("vendor").toString(), shop.get("shop_specialization").toString()));
                    }
                    shopAdapter.setShops(shops);
                    shopAdapter.notifyDataSetChanged();
                    refreshLayout.setRefreshing(false);
                } else {
                    Toast.makeText(MainActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();

                }
            }
        });*/
    }

    private void loadShops() {
//        refreshLayout.setRefreshing(true);
        shops.clear();
        shopAdapter.notifyDataSetChanged();
/*

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Please enable GPS to proceed")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Cancel", null);
            builder.show();

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                    (MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

            } else {
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();


                } else if (location1 != null) {
                    latitude = location1.getLatitude();
                    longitude = location1.getLongitude();

                } else if (location2 != null) {
                    latitude = location2.getLatitude();
                    longitude = location2.getLongitude();
                } else {

                    Toast.makeText(MainActivity.this, "Unable to trace your location", Toast.LENGTH_SHORT).show();

                }
                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    String pincode = addresses.get(0).getPostalCode();

                    shops.clear();
                    shopAdapter.notifyDataSetChanged();

                    db.collection("shops_index").whereEqualTo("shop_pincode", pincode)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<DocumentSnapshot> documents = task.getResult().getDocuments();

                                for (DocumentSnapshot document : documents) {
                                    Map<String, Object> shop = document.getData();
                                    shops.add(new Shop(Boolean.parseBoolean(shop.get("quick_delivery").toString()),
                                            shop.get("shop_pincode").toString(), document.getId(), shop.get("shop_name").toString(),
                                            shop.get("shop_timings_from").toString(), shop.get("shop_image_url").toString(), shop.get("shop_address").toString(),
                                            shop.get("vendor").toString(), shop.get("shop_specialization").toString(),
                                            Double.parseDouble(shop.get("minimum_order").toString())));
                                }
                                shopAdapter.setShops(shops);
                                shopAdapter.notifyDataSetChanged();
                                refreshLayout.setRefreshing(false);
                            } else {
                                Toast.makeText(MainActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                } catch (Exception e) {
                    Toast.makeText(this, "Fetching location, please wait...", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "loadShops: " + e.getLocalizedMessage());
                }
            }
        }
*/
/*

        db.collection("shops_index").document("BHYRAVA_PROVISIONS")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    Map<String, Object> shop = task.getResult().getData();
                    shops.add(new Shop(Boolean.parseBoolean(shop.get("quick_delivery").toString()),
                            shop.get("shop_pincode").toString(), task.getResult().getId(), shop.get("shop_name").toString(),
                            shop.get("shop_timings_from").toString(), shop.get("shop_image_url").toString(), shop.get("shop_address").toString(),
                            shop.get("vendor").toString(), shop.get("shop_specialization").toString(),
                            Double.parseDouble(shop.get("minimum_order").toString())));
                    shopAdapter.setShops(shops);
                    shopAdapter.notifyDataSetChanged();
                    refreshLayout.setRefreshing(false);
                } else {
                    Toast.makeText(MainActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();

                }
            }
        });
*/

        db.collection("shops_index").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> shoplist = task.getResult().getDocuments();
                for (DocumentSnapshot document : shoplist) {
                    Map<String, Object> shop = document.getData();
                    shops.add(new Shop(Boolean.parseBoolean(shop.get("quick_delivery").toString()),
                            shop.get("shop_pincode").toString(), document.getId(), shop.get("shop_name").toString(),
                            shop.get("shop_timings_from").toString(), shop.get("shop_image_url").toString(), shop.get("shop_address").toString(),
                            shop.get("vendor").toString(), shop.get("shop_specialization").toString(),
                            Double.parseDouble(shop.get("minimum_order").toString())));
                    shopAdapter.setShops(shops);
                    shopAdapter.notifyDataSetChanged();
                    refreshLayout.setRefreshing(false);
                }
            }
        });

    }

    private void filter(String text) {
        List<Shop> temp = new ArrayList<>();
        for (Shop shop : shops) {
            if (shop.getShopName().toLowerCase().contains(text.toLowerCase())) {
                temp.add(shop);
            }
        }
        shopAdapter.setShops(temp);
    }
}
