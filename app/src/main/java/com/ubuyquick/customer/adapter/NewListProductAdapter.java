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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ubuyquick.customer.R;
import com.ubuyquick.customer.model.ListProduct;
import com.ubuyquick.customer.model.NewListProduct;
import com.ubuyquick.customer.utils.UniversalImageLoader;

import java.util.ArrayList;
import java.util.List;

public class NewListProductAdapter extends RecyclerView.Adapter<NewListProductAdapter.ViewHolder> {

    private static final String TAG = "NewListProductAdapter";

    private Context context;
    private List<NewListProduct> newListProducts;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public NewListProductAdapter(Context context) {
        this.context = context;
        this.newListProducts = new ArrayList<>();
        ImageLoader.getInstance().init(new UniversalImageLoader(context).getConfig());
    }

    public void setNewListProducts(List<NewListProduct> newListProducts) {
        this.newListProducts = newListProducts;
        notifyDataSetChanged();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_product_name;
        private TextView tv_product_mrp;
        private TextView tv_product_measure;
        private EditText tv_product_quantity;
        private ImageButton btn_delete;

        public ViewHolder(View itemView) {
            super(itemView);

            this.tv_product_name = (TextView) itemView.findViewById(R.id.tv_product_name);
            this.tv_product_quantity = (EditText) itemView.findViewById(R.id.tv_product_quantity);
            this.tv_product_mrp = (TextView) itemView.findViewById(R.id.tv_product_mrp);
            this.tv_product_measure= (TextView) itemView.findViewById(R.id.tv_product_measure);
            this.btn_delete = (ImageButton) itemView.findViewById(R.id.btn_delete);

            this.tv_product_quantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        newListProducts.get(getAdapterPosition()).setProductQuantity(Integer.parseInt(s.toString()));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            });

            this.btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String product_id = newListProducts.get(getAdapterPosition()).getProductId();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Remove product from the list?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
//                                            .collection("current_list").document(product_id).delete();
                                    newListProducts.remove(getAdapterPosition());
                                    notifyItemRemoved(getAdapterPosition());
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();
                }
            });

        }

        public void bind(NewListProduct newListProduct) {
            tv_product_name.setText(newListProduct.getProductName());
            tv_product_quantity.setText(newListProduct.getProductQuantity() + "");
            tv_product_measure.setText(newListProduct.getProductMeasure());
            tv_product_mrp.setText(newListProduct.getProductMrp() + "");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(this.context).inflate(R.layout.card_new_list_product, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(this.newListProducts.get(position));
    }

    @Override
    public int getItemCount() {
        return newListProducts.size();
    }
}