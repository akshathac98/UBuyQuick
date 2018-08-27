package com.ubuyquick.customer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreFragment extends Fragment {

    private static final String TAG = "StoreFragment";

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private RecyclerView rv_shops;
    private ShopAdapter shopAdapter;
    private List<Shop> shops;

    public StoreFragment() {
    }

    public static StoreFragment newInstance() {
        StoreFragment fragment = new StoreFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store, container, false);

        rv_shops = view.findViewById(R.id.rv_shops);
        shopAdapter = new ShopAdapter(view.getContext());
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
//                        shops.add(new Shop(shop.get("status").toString(), "560091", "BHYRAVA_PROVISIONS", "BHYRAVA PROVISIONS", shop.get("timings").toString(), shop.get("image_url").toString(), "Siddik Stores, Milk Dairy Bus Stop, Hegganahalli Main Road, Peenya 2nd Stage, Bangalore - 560091", shop.get("vendor").toString()));
                        shopAdapter.setShops(shops);
                    }
                } else {
                    Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();

                }
            }
        });

        return view;
    }
}
