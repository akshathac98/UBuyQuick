package com.ubuyquick.customer;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ubuyquick.customer.adapter.CreditNoteAdapter;
import com.ubuyquick.customer.model.CreditNote;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreditsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private RecyclerView rv_credits;
    private CreditNoteAdapter creditNoteAdapter;
    private List<CreditNote> creditNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        rv_credits = (RecyclerView) findViewById(R.id.rv_credits);
        creditNotes = new ArrayList<>();
        creditNoteAdapter = new CreditNoteAdapter(this);
        rv_credits.setAdapter(creditNoteAdapter);

        db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                .collection("credit_notes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                for (DocumentSnapshot document : documents) {
                    Map<String, Object> credit = document.getData();
                    creditNotes.add(new CreditNote(credit.get("name").toString(), credit.get("shop_id").toString()
                            .replace('_', ' '), false,
                            document.getId(), Double.parseDouble(credit.get("balance").toString())));
                }
                creditNoteAdapter.setCreditNotes(creditNotes);
            }
        });
    }
}
