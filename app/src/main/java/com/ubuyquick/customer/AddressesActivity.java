package com.ubuyquick.customer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ubuyquick.customer.adapter.AddressAdapter;
import com.ubuyquick.customer.model.Address;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressesActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private RecyclerView rv_addresses;
    private List<Address> addressList;
    private AddressAdapter addressAdapter;

    private Button btn_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addresses);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        rv_addresses = (RecyclerView) findViewById(R.id.rv_addresses);
        addressList = new ArrayList<>();
        addressAdapter = new AddressAdapter(this);
        rv_addresses.setAdapter(addressAdapter);
        btn_add = (Button) findViewById(R.id.btn_add);

        db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                .collection("addresses").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                for (DocumentSnapshot document : documents) {
                    Map<String, Object> address = document.getData();
                    addressList.add(new Address(document.getId(),
                            address.get("name").toString(),
                            address.get("contact").toString(),
                            address.get("building_no").toString(),
                            address.get("address1").toString(),
                            address.get("address2").toString(),
                            address.get("pincode").toString()));
                }
                addressAdapter.setAddressList(addressList);
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(AddressesActivity.this);

                View viewInflated = LayoutInflater.from(AddressesActivity.this).inflate(R.layout.dialog_address, null, false);
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
                                final String name = et_name.getText().toString();
                                final String number = et_number.getText().toString();
                                final String address1 = et_address1.getText().toString();
                                final String address2 = et_address2.getText().toString();
                                final String pincode = et_pincode.getText().toString();
                                final String contact = et_contact.getText().toString();

                                if (name.isEmpty() || number.isEmpty() || address1.isEmpty() || address2.isEmpty() || pincode.isEmpty() || contact.isEmpty()) {
                                    Toast.makeText(AddressesActivity.this, "All fields required.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Map<String, Object> addr = new HashMap<>();
                                    addr.put("pincode", pincode);
                                    addr.put("name", name);
                                    addr.put("contact", contact);
                                    addr.put("address1", address1);
                                    addr.put("address2", address2);
                                    addr.put("building_no", number);
                                    db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                            .collection("addresses").add(addr).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            addressList.add(new Address(task.getResult().getId(),
                                                    name, contact, number, address1, address2, pincode));
                                            addressAdapter.setAddressList(addressList);
                                        }
                                    });


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
    }
}
