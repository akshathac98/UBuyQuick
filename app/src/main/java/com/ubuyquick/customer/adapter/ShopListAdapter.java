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
import com.ubuyquick.customer.model.ShopProduct;
import com.ubuyquick.customer.utils.UniversalImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopListAdapter extends RecyclerView.Adapter<ShopListAdapter.ViewHolder> {

    private static final String TAG = "ShopListAdapter";

    private Context context;
    private List<ShopProduct> shopProducts;
    private String shop_id;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public ShopListAdapter(Context context, String shop_id) {
        this.context = context;
        this.shop_id = shop_id;
        this.shopProducts = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        ImageLoader.getInstance().init(new UniversalImageLoader(context).getConfig());
    }

    public void setListProducts(List<ShopProduct> shopProducts) {
        this.shopProducts = shopProducts;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_product_name;
        private EditText tv_product_quantity;
        private TextView tv_product_mrp;
        private TextView tv_product_measure;
        private ImageView img_product;
        private ImageButton btn_remove;

        public ViewHolder(View itemView) {
            super(itemView);

            this.tv_product_name = (TextView) itemView.findViewById(R.id.tv_product_name);
            this.tv_product_quantity = (EditText) itemView.findViewById(R.id.tv_product_quantity);
            this.tv_product_mrp = (TextView) itemView.findViewById(R.id.tv_product_mrp);
            this.tv_product_measure = (TextView) itemView.findViewById(R.id.tv_product_measure);
            this.img_product = (ImageView) itemView.findViewById(R.id.img_product);
            this.btn_remove = (ImageButton) itemView.findViewById(R.id.btn_delete);
//
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
//                        Shop clickedShop = listProducts.get(getAdapterPosition());
//                        Intent i = new Intent((Activity) context, InventoryFragment2.class);
//                        i.putExtra("SHOP_ID", clickedShop.getShopId());
//                        i.putExtra("SHOP_VENDOR", clickedShop.getShopVendor());
//                        i.putExtra("SHOP_NAME", clickedShop.getShopName());
//                        context.startActivity(i);
//                    }
//                }
//            });

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
                        shopProducts.get(getAdapterPosition()).setProductQuantity(Integer.parseInt(s.toString()));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            });

            this.btn_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String product_id = shopProducts.get(getAdapterPosition()).getProductId();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Remove product from the list?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                            .collection("shop_lists").document(shop_id).collection("lists").
                                            document(product_id).delete();
                                    shopProducts.remove(getAdapterPosition());
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

        public void bind(ShopProduct shopProduct) {
//            UniversalImageLoader.setImage(shopProduct.getProductImageUrl(), this.img_product);
            tv_product_name.setText(shopProduct.getProductName());
            tv_product_measure.setText(shopProduct.getProductMeasure());
            tv_product_quantity.setText(shopProduct.getProductQuantity() + "");
            tv_product_mrp.setText("\u20B9" + shopProduct.getProductMrp());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(this.context).inflate(R.layout.card_shop_list2, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(this.shopProducts.get(position));
    }

    @Override
    public int getItemCount() {
        return shopProducts.size();
    }
}