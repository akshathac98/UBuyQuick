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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ubuyquick.customer.R;
import com.ubuyquick.customer.model.MainSearchProduct;
import com.ubuyquick.customer.model.NewListProduct;
import com.ubuyquick.customer.utils.UniversalImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryProductAdapter extends RecyclerView.Adapter<InventoryProductAdapter.ViewHolder> {

    private static final String TAG = "InventoryProductAdapter";

    private Context context;
    private List<MainSearchProduct> products;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String shop_id;

    public InventoryProductAdapter(Context context, String shop_id) {
        this.context = context;
        this.shop_id = shop_id;
        this.products = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        ImageLoader.getInstance().init(new UniversalImageLoader(context).getConfig());

    }

    public void setProducts(List<MainSearchProduct> products) {
        this.products = products;
        notifyDataSetChanged();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_product_name;
        private TextView tv_product_mrp;
        private TextView tv_product_measure;
        private ImageView img_product;

        public ViewHolder(View itemView) {
            super(itemView);

            this.tv_product_name = (TextView) itemView.findViewById(R.id.tv_product_name);
            this.tv_product_mrp = (TextView) itemView.findViewById(R.id.tv_product_mrp);
            this.tv_product_measure= (TextView) itemView.findViewById(R.id.tv_product_measure);
            this.img_product= (ImageView) itemView.findViewById(R.id.tv_product_image);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final MainSearchProduct searchProduct = products.get(getAdapterPosition());

                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);

                    View viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_product_details, null, false);
                    final TextView tv_name = (TextView) viewInflated.findViewById(R.id.tv_name);
                    final TextView tv_mrp = (TextView) viewInflated.findViewById(R.id.tv_price);
                    final ImageView tv_image = (ImageView) viewInflated.findViewById(R.id.tv_image);
                    final TextView tv_measure = (TextView) viewInflated.findViewById(R.id.tv_measure);
                    final EditText et_quantity = (EditText) viewInflated.findViewById(R.id.et_quantity);

                    UniversalImageLoader.setImage(searchProduct.getProductUrl(), tv_image);
                    tv_name.setText(searchProduct.getProductName());
                    tv_measure.setText(searchProduct.getProductMeasure());
                    tv_mrp.setText("\u20B9" + searchProduct.getProductMrp());

                    builder.setView(viewInflated);
                    builder.setNegativeButton("Cancel", null)
                            .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (et_quantity.getText().toString().isEmpty())
                                        Toast.makeText(context, "Please enter quantity to add", Toast.LENGTH_SHORT).show();
                                    else {
                                        Map<String, Object> product = new HashMap<>();
                                        product.put("product_measure", searchProduct.getProductMeasure());
                                        product.put("product_name", searchProduct.getProductName());
                                        product.put("product_quantity", Integer.parseInt(et_quantity.getText().toString()));
                                        product.put("product_mrp", searchProduct.getProductMrp());
                                        db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                .collection("shop_lists").document(shop_id).collection("lists").add(product);
                                    }
                                }
                            });
                    builder.show();
                }
            });
        }

        public void bind(MainSearchProduct product) {
            UniversalImageLoader.setImage(product.getProductUrl(), this.img_product);
            tv_product_name.setText(product.getProductName());
            tv_product_measure.setText(product.getProductMeasure());
            tv_product_mrp.setText("\u20B9" + product.getProductMrp());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(this.context).inflate(R.layout.card_inventory_product, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(this.products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}