package com.ubuyquick.customer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ubuyquick.customer.R;
import com.ubuyquick.customer.ShopActivity;
import com.ubuyquick.customer.model.Shop;
import com.ubuyquick.customer.utils.UniversalImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> implements Filterable {

    private static final String TAG = "ShopAdapter";

    private Context context;
    private List<Shop> shops;
    private List<Shop> shopsFiltered;

    public ShopAdapter(Context context) {
        this.context = context;
        this.shops = new ArrayList<>();
        this.shopsFiltered = new ArrayList<>();
        ImageLoader.getInstance().init(new UniversalImageLoader(context).getConfig());
    }

    public void setShops(List<Shop> shops) {
        this.shops = shops;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_shop_name;
        private TextView tv_quick_delivery;
        private ImageView img_shop;
        private TextView tv_shop_timings;
        private TextView tv_shop_address;
        private TextView tv_shop_spec;
        private TextView tv_minimum_order;

        public ViewHolder(View itemView) {
            super(itemView);

            this.tv_minimum_order = (TextView) itemView.findViewById(R.id.tv_minimum_order);
            this.tv_shop_name = (TextView) itemView.findViewById(R.id.tv_shop_name);
            this.tv_shop_spec = (TextView) itemView.findViewById(R.id.tv_shop_specialization);
            this.img_shop = (ImageView) itemView.findViewById(R.id.img_shop);
            this.tv_quick_delivery = (TextView) itemView.findViewById(R.id.tv_quick_delivery);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        Shop clickedShop = shops.get(getAdapterPosition());
                        Intent i = new Intent((Activity) context, ShopActivity.class);
                        i.putExtra("SHOP_ID", clickedShop.getShopId());
                        i.putExtra("SHOP_VENDOR", clickedShop.getShopVendor());
                        i.putExtra("SHOP_NAME", clickedShop.getShopName());
                        i.putExtra("QUICK_DELIVERY", clickedShop.getQuickDelivery());
                        i.putExtra("MINIMUM_ORDER", clickedShop.getMinimumOrder());
                        context.startActivity(i);
                    }
                }
            });

        }


        public void bind(Shop shop) {
            UniversalImageLoader.setImage(shop.getShopImageUrl(), this.img_shop);
            tv_shop_name.setText(shop.getShopName());
            tv_minimum_order.setText("Min Order: \u20B9" + shop.getMinimumOrder());
            if (!shop.getQuickDelivery())
                tv_quick_delivery.setVisibility(View.INVISIBLE);
            else
                tv_quick_delivery.setVisibility(View.VISIBLE);
            tv_shop_spec.setText(shop.getShopSpec());
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    shopsFiltered = shops;
                } else {
                    List<Shop> filteredShops = new ArrayList<>();
                    for (Shop shop : shops) {
                        if (shop.getShopName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredShops.add(shop);
                        }
                    }
                    shopsFiltered = filteredShops;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = shopsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                shopsFiltered = (ArrayList<Shop>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(this.context).inflate(R.layout.card_shop, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(this.shops.get(position));
    }

    @Override
    public int getItemCount() {
        return shops.size();
    }
}