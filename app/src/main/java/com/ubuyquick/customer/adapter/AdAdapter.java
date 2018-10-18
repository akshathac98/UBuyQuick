package com.ubuyquick.customer.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ubuyquick.customer.R;
import com.ubuyquick.customer.model.Ad;

import java.util.List;

public class AdAdapter extends RecyclerView.Adapter<AdAdapter.ViewHolder> {

    private static final String TAG = "AdAdapter";

    private Context context;
    private List<Ad> ads;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public AdAdapter(Context context, List<Ad> ads) {
        this.context = context;
        this.ads = ads;
    }

    public void setAds(List<Ad> ads) {
        this.ads = ads;
        notifyDataSetChanged();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private CardView main_layout;
        private ImageView img_ad;

        public ViewHolder(View itemView) {
            super(itemView);

            main_layout = itemView.findViewById(R.id.main_layout);
            img_ad = itemView.findViewById(R.id.img_ad);
        }

        public void bind(Ad ad) {
            img_ad.setImageDrawable(context.getDrawable(ad.getAdId()));

            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;

//            CardView.LayoutParams params = (CardView.LayoutParams) main_layout.getLayoutParams();
//            params.width = width;
//            main_layout.setLayoutParams(params);
        }
    }

    @NonNull
    @Override
    public AdAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(this.context).inflate(R.layout.card_ad, parent, false);
        return new AdAdapter.ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdAdapter.ViewHolder holder, int position) {
        holder.bind(ads.get(position));
    }

    @Override
    public int getItemCount() {
        return ads.size();
    }

}
