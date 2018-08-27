package com.ubuyquick.customer.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ubuyquick.customer.model.CreditNote;
import com.ubuyquick.customer.utils.MultiChoiceHelper;
import com.ubuyquick.customer.R;
import com.ubuyquick.customer.model.CreditNote;
import com.ubuyquick.customer.utils.MultiChoiceHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreditNoteAdapter extends RecyclerView.Adapter<CreditNoteAdapter.ViewHolder> implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "CreditNoteAdapter";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Context context;
    private List<CreditNote> creditNotes;

    private String shop_id, vendor_id, number;
    private int LOGIN_MODE = 0;

    public CreditNoteAdapter(Context context) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        this.context = context;
        this.shop_id = shop_id;
        this.vendor_id = vendor_id;
        creditNotes = new ArrayList<>();
    }

    public void setCreditNotes(List<CreditNote> creditNotes) {
        this.creditNotes = creditNotes;
        notifyDataSetChanged();
    }

    class ViewHolder extends MultiChoiceHelper.ViewHolder {
        private TextView tv_customer_name;
        private TextView tv_shop_name;
        private TextView tv_credit;

        private ImageButton btn_clear;

        public ViewHolder(View itemView) {
            super(itemView);

            this.tv_customer_name = (TextView) itemView.findViewById(R.id.tv_name);
            this.tv_shop_name = (TextView) itemView.findViewById(R.id.tv_shop);
            this.tv_credit = (TextView) itemView.findViewById(R.id.tv_credit);
        }

        public void bind(final CreditNote creditNote) {
            this.tv_customer_name.setText(creditNote.getCustomerName());
            this.tv_shop_name.setText(creditNote.getShopName());
            this.tv_credit.setText("\u20B9" + creditNote.getCredit());

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Permission required to send sms", Toast.LENGTH_SHORT).show();
                } else {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage("+919008003968", null, "Credit balance pending for Bhyrava provisions", null, null);
                }
                break;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.card_note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(this.creditNotes.get(position));
    }

    @Override
    public int getItemCount() {
        return creditNotes.size();
    }
}
