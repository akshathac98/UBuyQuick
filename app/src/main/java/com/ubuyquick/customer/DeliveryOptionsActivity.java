package com.ubuyquick.customer;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ubuyquick.customer.model.Shop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DeliveryOptionsActivity extends AppCompatActivity {

    private static final String TAG = "DeliveryOptionsActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Geocoder geocoder;
    private LocationManager locationManager;
    private static final int REQUEST_LOCATION = 1;
    private Double latitude, longitude;

    private String shop_id;
    private String shop_vendor;
    private List<String> addresses;
    private List<Address> address_locations;
    private String selected_address = null;
    private int i = 0;

    private Button btn_new_address, btn_locate, btn_proceed;
    private TextView tv_address;
    private RadioGroup rg_slots, rg_addresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_options);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_up);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        geocoder = new Geocoder(this, Locale.getDefault());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        rg_slots = (RadioGroup) findViewById(R.id.rg_slots);
        rg_addresses = (RadioGroup) findViewById(R.id.rg_addresses);

        btn_locate = (Button) findViewById(R.id.btn_locate);
        btn_proceed = (Button) findViewById(R.id.btn_proceed);
        btn_new_address = (Button) findViewById(R.id.btn_new_address);
        tv_address = (TextView) findViewById(R.id.tv_address);
        addresses = new ArrayList<>();

        db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3)).collection("addresses")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> addrs = task.getResult().getDocuments();
                for (; i < addrs.size(); i++) {
                    RadioButton rb = new RadioButton(DeliveryOptionsActivity.this);
                    Map<String, Object> addrss = addrs.get(i).getData();
                    final String address = "#" + addrss.get("building_no").toString() + ", " + addrss.get("address1").toString() + ", " + addrss.get("address2").toString()
                            + ", " + addrss.get("pincode").toString();
                    addresses.add(address);
                    rb.setText(address);
                    rb.setTextSize(20);
                    rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            tv_address.setText(address);
                        }
                    });
                    rg_addresses.addView(rb);
                }
            }
        });

        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rg_slots.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(DeliveryOptionsActivity.this, "Please select delivery slot", Toast.LENGTH_SHORT).show();
                } else if (rg_addresses.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(DeliveryOptionsActivity.this, "Please select delivery address", Toast.LENGTH_SHORT).show();
                } else {
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(DeliveryOptionsActivity.this);
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
                        if (ActivityCompat.checkSelfPermission(DeliveryOptionsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                                (DeliveryOptionsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(DeliveryOptionsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

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
                                Toast.makeText(DeliveryOptionsActivity.this, "Unable to trace your location", Toast.LENGTH_SHORT).show();
                            }

                        }
                        if (latitude != null || longitude != null) {
                            Intent i = new Intent(DeliveryOptionsActivity.this, PaymentOptions.class);
                            i.putExtra("shop_id", getIntent().getStringExtra("shop_id"));
                            i.putExtra("shop_vendor", getIntent().getStringExtra("shop_vendor"));
                            i.putExtra("latitude", latitude + "");
                            i.putExtra("longitude", longitude + "");
                            i.putExtra("checkout_list", getIntent().getStringExtra("checkout_list"));
                            i.putExtra("address", ((RadioButton) findViewById(rg_addresses.getCheckedRadioButtonId())).getText());
                            i.putExtra("slot", ((RadioButton) findViewById(rg_slots.getCheckedRadioButtonId())).getText());
                            startActivityForResult(i, 1);
                            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                        }
                    }
                }


            }
        });

        btn_locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(DeliveryOptionsActivity.this);
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
                    if (ActivityCompat.checkSelfPermission(DeliveryOptionsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                            (DeliveryOptionsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(DeliveryOptionsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

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

                            Toast.makeText(DeliveryOptionsActivity.this, "Unable to trace your location", Toast.LENGTH_SHORT).show();

                        }
                        try {
                            address_locations = geocoder.getFromLocation(latitude, longitude, 1);

                            AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryOptionsActivity.this);

                            View viewInflated = LayoutInflater.from(DeliveryOptionsActivity.this).inflate(R.layout.dialog_address, null, false);
                            final TextInputEditText et_name = (TextInputEditText) viewInflated.findViewById(R.id.et_name);

                            final TextInputEditText et_number = (TextInputEditText) viewInflated.findViewById(R.id.et_number);

                            final TextInputEditText et_address1 = (TextInputEditText) viewInflated.findViewById(R.id.et_address1);
                            et_address1.setText(address_locations.get(0).getAddressLine(0));

                            final TextInputEditText et_address2 = (TextInputEditText) viewInflated.findViewById(R.id.et_address2);
                            et_address1.setText(address_locations.get(0).getAddressLine(1));

                            final TextInputEditText et_pincode = (TextInputEditText) viewInflated.findViewById(R.id.et_pincode);
                            et_pincode.setText(address_locations.get(0).getPostalCode());

                            final TextInputEditText et_contact = (TextInputEditText) viewInflated.findViewById(R.id.et_contact);
                            et_contact.setText(mAuth.getCurrentUser().getPhoneNumber().substring(3));

                            builder.setView(viewInflated);
                            builder.setMessage("Add New Address")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            String name = et_name.getText().toString();
                                            String number = et_number.getText().toString();
                                            String address1 = et_address1.getText().toString();
                                            String address2 = et_address2.getText().toString();
                                            String pincode = et_pincode.getText().toString();
                                            String contact = et_contact.getText().toString();

                                            if (name.isEmpty() || number.isEmpty() || address1.isEmpty() || address2.isEmpty() || pincode.isEmpty() || contact.isEmpty()) {
                                                Toast.makeText(DeliveryOptionsActivity.this, "All fields required.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Map<String, Object> addr = new HashMap<>();
                                                addr.put("pincode", pincode);
                                                addr.put("address1", address1);
                                                addr.put("address2", address2);
                                                addr.put("building_no", number);
                                                db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                        .collection("addresses").add(addr);
                                                selected_address = name + ",\n#" + number + ", " + address1 + ",\n" + address2
                                                        + ", " + pincode + ",\n" + contact;

                                                final String address = "#" + number + ", " + address1 + ", " + address2
                                                        + ", " + pincode;
                                                RadioButton rb = new RadioButton(DeliveryOptionsActivity.this);
                                                rb.setText(address);

                                                tv_address.setText(selected_address);
                                                addresses.add(selected_address);
                                                rg_addresses.addView(rb);
                                            }
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                        }
                                    }).show();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        btn_new_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryOptionsActivity.this);

                View viewInflated = LayoutInflater.from(DeliveryOptionsActivity.this).inflate(R.layout.dialog_address, null, false);
                final TextInputEditText et_name = (TextInputEditText) viewInflated.findViewById(R.id.et_name);
                final TextInputEditText et_number = (TextInputEditText) viewInflated.findViewById(R.id.et_number);
                final TextInputEditText et_address1 = (TextInputEditText) viewInflated.findViewById(R.id.et_address1);
                final TextInputEditText et_address2 = (TextInputEditText) viewInflated.findViewById(R.id.et_address2);
                final TextInputEditText et_pincode = (TextInputEditText) viewInflated.findViewById(R.id.et_pincode);
                final TextInputEditText et_contact = (TextInputEditText) viewInflated.findViewById(R.id.et_contact);

                builder.setView(viewInflated);
                builder.setMessage("Add New Address")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String name = et_name.getText().toString();
                                String number = et_number.getText().toString();
                                String address1 = et_address1.getText().toString();
                                String address2 = et_address2.getText().toString();
                                String pincode = et_pincode.getText().toString();
                                String contact = et_contact.getText().toString();

                                if (name.isEmpty() || number.isEmpty() || address1.isEmpty() || address2.isEmpty() || pincode.isEmpty() || contact.isEmpty()) {
                                    Toast.makeText(DeliveryOptionsActivity.this, "All fields required.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Map<String, Object> addr = new HashMap<>();
                                    addr.put("pincode", pincode);
                                    addr.put("name", name);
                                    addr.put("contact", contact);
                                    addr.put("address1", address1);
                                    addr.put("address2", address2);
                                    addr.put("building_no", number);
                                    db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                            .collection("addresses").add(addr);
                                    selected_address = name + ",\n#" + number + ", " + address1 + ",\n" + address2
                                            + ", " + pincode + ",\n" + contact;

                                    final String address = "#" + number + ", " + address1 + ", " + address2
                                            + ", " + pincode;
                                    RadioButton rb = new RadioButton(DeliveryOptionsActivity.this);
                                    rb.setText(address);

                                    tv_address.setText(selected_address);
                                    addresses.add(selected_address);
                                    rg_addresses.addView(rb);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        }).show();
            }
        });

        db.collection("vendors").document(getIntent().getStringExtra("shop_vendor"))
                .collection("shops").document(getIntent().getStringExtra("shop_id")).collection("time_slots").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> slots = task.getResult().getDocuments();
                        for (DocumentSnapshot slot : slots) {
                            RadioButton rb = new RadioButton(DeliveryOptionsActivity.this);
                            rb.setText(slot.getData().get("from").toString() + " to " + slot.getData().get("to").toString());
                            rb.setTextSize(20);
                            rg_slots.addView(rb);
                        }
                        db.collection("vendors").document(getIntent().getStringExtra("shop_vendor"))
                                .collection("shops").document(getIntent().getStringExtra("shop_id")).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (Boolean.parseBoolean(task.getResult().getData().get("quick_delivery").toString())) {
                                            RadioButton radioButton = new RadioButton(DeliveryOptionsActivity.this);
                                            radioButton.setText("Quick Delivery");
                                            radioButton.setTextSize(20);
                                            rg_slots.addView(radioButton);
                                        }
                                        RadioButton radioButton = new RadioButton(DeliveryOptionsActivity.this);
                                        radioButton.setText("Pick Up");
                                        radioButton.setTextSize(20);
                                        rg_slots.addView(radioButton);
                                    }
                                });
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Order placed successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
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
