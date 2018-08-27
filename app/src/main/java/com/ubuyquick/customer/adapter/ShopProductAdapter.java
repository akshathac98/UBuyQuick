package com.ubuyquick.customer.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ubuyquick.customer.R;
import com.ubuyquick.customer.model.ListProduct;
import com.ubuyquick.customer.model.Shop;
import com.ubuyquick.customer.model.ShopProduct;
import com.ubuyquick.customer.utils.UniversalImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopProductAdapter extends RecyclerView.Adapter<ShopProductAdapter.ViewHolder> {

    private static final String TAG = "ListProductAdapter";

    private Context context;
    private List<ShopProduct> shopProducts;
    private String shop_id;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public ShopProductAdapter(Context context, String shop_id) {
        this.context = context;
        this.shop_id = shop_id;
        this.shopProducts = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        ImageLoader.getInstance().init(new UniversalImageLoader(context).getConfig());
    }

    public void setShopProducts(List<ShopProduct> shopProducts) {
        this.shopProducts = shopProducts;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_product_name;
        private TextView tv_product_quantity;
        private TextView tv_product_mrp;
        private ImageView img_product;
        private ImageButton btn_plus;
        private ImageButton btn_minus;
        private Button btn_add;

        public ViewHolder(View itemView) {
            super(itemView);

            this.tv_product_name = (TextView) itemView.findViewById(R.id.tv_product_name);
            this.tv_product_quantity = (TextView) itemView.findViewById(R.id.tv_quantity);
            this.tv_product_mrp = (TextView) itemView.findViewById(R.id.tv_product_mrp);
            this.img_product = (ImageView) itemView.findViewById(R.id.img_product);
            this.btn_minus = (ImageButton) itemView.findViewById(R.id.btn_minus);
            this.btn_plus = (ImageButton) itemView.findViewById(R.id.btn_plus);
            this.btn_add = (Button) itemView.findViewById(R.id.btn_add);
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

            this.btn_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        ShopProduct clickedProduct = shopProducts.get(getAdapterPosition());
                        int quantity = clickedProduct.getProductQuantity();
                        tv_product_quantity.setText(quantity + 1 + "");
                        clickedProduct.setProductQuantity(quantity + 1);
                    }
                }
            });

            this.btn_minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        ShopProduct clickedProduct = shopProducts.get(getAdapterPosition());
                        if (Integer.parseInt(tv_product_quantity.getText().toString()) != 0) {
                            int quantity = clickedProduct.getProductQuantity();
                            tv_product_quantity.setText(quantity - 1 + "");
                            clickedProduct.setProductQuantity(quantity - 1);
                        }
                    }
                }
            });

            this.btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, Object> list_product = new HashMap<>();
                    ShopProduct shopProduct = shopProducts.get(getAdapterPosition());
                    list_product.put("product_name", shopProduct.getProductName());
                    list_product.put("product_mrp", shopProduct.getProductMrp());
                    list_product.put("product_id", shopProduct.getProductId());
                    list_product.put("product_quantity", shopProduct.getProductQuantity());
                    list_product.put("product_measure", shopProduct.getProductMeasure());
                    db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                    .collection("shop_lists").document(shop_id).collection("list").
                            document(shopProduct.getProductId()).set(list_product);
                }
            });

        }

        public void bind(ShopProduct shopProduct) {
            tv_product_name.setText(shopProduct.getProductName());
            tv_product_quantity.setText(shopProduct.getProductQuantity() + "");
            tv_product_mrp.setText("M.R.P : Rs. " + shopProduct.getProductMrp());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(this.context).inflate(R.layout.card_shop_product, parent, false);
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