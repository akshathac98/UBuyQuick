package com.ubuyquick.customer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ubuyquick.customer.adapter.ListAdapter;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadListActivity extends AppCompatActivity {

    private static final String TAG = "LoadListActivity";

    private RecyclerView rv_lists;
    private ListAdapter listAdapter;
    private List<com.ubuyquick.customer.model.List> lists;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_list);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_up);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rv_lists = (RecyclerView) findViewById(R.id.rv_lists);
        listAdapter = new ListAdapter(this);
        lists = new ArrayList<>();
        rv_lists.setAdapter(listAdapter);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3)).collection("lists")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                for (DocumentSnapshot document : documents) {
                    Map<String, Object> list = document.getData();
                    lists.add(new com.ubuyquick.customer.model.List(list.get("list_name").toString(),
                            Integer.parseInt(list.get("list_count").toString()), document.getId(),
                            list.get("created_at").toString().substring(0, 11)));
                }
                listAdapter.setLists(lists);
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(mListIdReceiver, new IntentFilter("list id transaction"));

    }

    public BroadcastReceiver mListIdReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String list_id = intent.getStringExtra("list_id");
            sendResult(list_id);
        }
    };

    private void sendResult(String list_id) {
        Intent i = new Intent("list id transaction");
        i.putExtra("list_id", list_id);
        setResult(Activity.RESULT_OK, i);
        finish();
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
