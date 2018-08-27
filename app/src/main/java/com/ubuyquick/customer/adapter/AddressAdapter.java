package com.ubuyquick.customer.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ubuyquick.customer.AddressesActivity;
import com.ubuyquick.customer.R;
import com.ubuyquick.customer.model.Address;
import com.ubuyquick.customer.model.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private static final String TAG = "AddressAdapter";

    private Context context;
    private List<Address> addressList;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public AddressAdapter(Context context) {
        this.context = context;
        this.addressList = new ArrayList<>();
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
        notifyDataSetChanged();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name, tv_contact, tv_building, tv_address1, tv_address2, tv_pincode;
        private ImageButton btn_edit, btn_delete;

        public ViewHolder(View itemView) {
            super(itemView);

            this.tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            this.tv_contact = (TextView) itemView.findViewById(R.id.tv_number);
            this.tv_building = (TextView) itemView.findViewById(R.id.tv_building);
            this.tv_address1 = (TextView) itemView.findViewById(R.id.tv_address1);
            this.tv_address2 = (TextView) itemView.findViewById(R.id.tv_address2);
            this.tv_pincode = (TextView) itemView.findViewById(R.id.tv_pincode);
            this.btn_delete = (ImageButton) itemView.findViewById(R.id.btn_delete);
            this.btn_edit = (ImageButton) itemView.findViewById(R.id.btn_edit);

            this.btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    final Address address = addressList.get(getAdapterPosition());
                    View viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_address, null, false);
                    final TextInputEditText et_name = (TextInputEditText) viewInflated.findViewById(R.id.et_name);
                    final TextInputEditText et_number = (TextInputEditText) viewInflated.findViewById(R.id.et_number);
                    final TextInputEditText et_address1 = (TextInputEditText) viewInflated.findViewById(R.id.et_address1);
                    final TextInputEditText et_address2 = (TextInputEditText) viewInflated.findViewById(R.id.et_address2);
                    final TextInputEditText et_pincode = (TextInputEditText) viewInflated.findViewById(R.id.et_pincode);
                    final TextInputEditText et_contact = (TextInputEditText) viewInflated.findViewById(R.id.et_contact);

                    et_name.setText(address.getName());
                    et_number.setText(address.getBuilding());
                    et_address1.setText(address.getAddress1());
                    et_address2.setText(address.getAddress2());
                    et_pincode.setText(address.getPincode());
                    et_contact.setText(address.getContact());

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
                                        Toast.makeText(context, "All fields required.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        final Map<String, Object> addr = new HashMap<>();
                                        addr.put("pincode", pincode);
                                        addr.put("name", name);
                                        addr.put("contact", contact);
                                        addr.put("address1", address1);
                                        addr.put("address2", address2);
                                        addr.put("building_no", number);
                                        db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                .collection("addresses").document(address.getAddressId()).update(addr)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        address.setName(name);
                                                        address.setAddress1(address1);
                                                        address.setAddress2(address2);
                                                        address.setBuilding(number);
                                                        address.setContact(contact);
                                                        address.setPincode(pincode);
                                                        notifyDataSetChanged();
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

            this.btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                    builder.setMessage("Delete address?");
                    builder.setNegativeButton("Cancel", null);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                    .collection("addresses").document(addressList.get(getAdapterPosition()).getAddressId())
                                    .delete();
                            addressList.remove(getAdapterPosition());
                            notifyItemRemoved(getAdapterPosition());
                        }
                    });
                    builder.show();
                }
            });

        }

        public void bind (Address address) {
            this.tv_name.setText(address.getName());
            this.tv_contact.setText(address.getContact());
            this.tv_building.setText(address.getBuilding());
            this.tv_address1.setText(address.getAddress1());
            this.tv_address2.setText(address.getAddress2());
            this.tv_pincode.setText(address.getPincode());
        }

    }

    @NonNull
    @Override
    public AddressAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(this.context).inflate(R.layout.card_address, parent, false);
        return new AddressAdapter.ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressAdapter.ViewHolder holder, int position) {
        holder.bind(this.addressList.get(position));
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

}
