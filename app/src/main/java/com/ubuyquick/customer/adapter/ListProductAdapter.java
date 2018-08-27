package com.ubuyquick.customer.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ubuyquick.customer.R;
import com.ubuyquick.customer.model.ListProduct;
import com.ubuyquick.customer.utils.UniversalImageLoader;
import com.ubuyquick.customer.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListProductAdapter extends RecyclerView.Adapter<ListProductAdapter.ViewHolder> {

    private static final String TAG = "ListProductAdapter";

    private Context context;
    private List<ListProduct> listProducts;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Utils.OnItemClick mCallback;
    private int selected_items = 0;

    public ListProductAdapter(Context context, Utils.OnItemClick listener) {
        this.context = context;
        this.mCallback = listener;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        this.listProducts = new ArrayList<>();
    }

    public void setListProducts(List<ListProduct> listProducts) {
        this.listProducts = listProducts;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_product_name;
        private EditText tv_product_quantity;
        private TextView tv_product_mrp;
        private CheckBox cb_selected;

        public ViewHolder(View itemView) {
            super(itemView);

            this.tv_product_name = (TextView) itemView.findViewById(R.id.tv_product_name);
            this.tv_product_quantity = (EditText) itemView.findViewById(R.id.et_quantity);
            this.tv_product_mrp = (TextView) itemView.findViewById(R.id.tv_product_mrp);
            this.cb_selected = (CheckBox) itemView.findViewById(R.id.cb_selected);

            this.cb_selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        selected_items++;
                        mCallback.onClick(selected_items);
                        listProducts.get(getAdapterPosition()).setSelected(true);
                        Map<String, Object> product = new HashMap<>();
                        ListProduct listProduct = listProducts.get(getAdapterPosition());
                        product.put("product_name", listProduct.getProductName());
                        product.put("product_mrp", listProduct.getProductMrp());
                        product.put("product_measure", listProduct.getProductMeasure());
                        product.put("product_quantity", listProduct.getProductQuantity());
                        product.put("product_id", listProduct.getProductId());
                        db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                .collection("temp_list").document(listProduct.getProductId()).set(product);
                    } else {
                        selected_items--;
                        mCallback.onClick(selected_items);
                        listProducts.get(getAdapterPosition()).setSelected(false);
                        ListProduct listProduct = listProducts.get(getAdapterPosition());
                        db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                .collection("temp_list").document(listProduct.getProductId()).delete();
                    }
                }
            });

            this.tv_product_quantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        listProducts.get(getAdapterPosition()).setProductQuantity(Integer.parseInt(s.toString()));
                    } catch (Exception e) {

                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        }

        public void bind(ListProduct listProduct) {
            tv_product_name.setText(listProduct.getProductName());
            tv_product_quantity.setText(listProduct.getProductQuantity() + "");
            tv_product_mrp.setText("\u20B9" + listProduct.getProductMrp());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(this.context).inflate(R.layout.card_list, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(this.listProducts.get(position));
    }

    @Override
    public int getItemCount() {
        return listProducts.size();
    }
}