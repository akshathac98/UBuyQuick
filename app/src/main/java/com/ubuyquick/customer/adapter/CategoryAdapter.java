package com.ubuyquick.customer.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ubuyquick.customer.R;
import com.ubuyquick.customer.SubCategoryActivity;
import com.ubuyquick.customer.model.Category;
import com.ubuyquick.customer.utils.UniversalImageLoader;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private static final String TAG = "CategoryAdapter";

    private Context context;
    private List<Category> categories;
    private ArrayAdapter<String> arrayAdapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public CategoryAdapter(Context context) {
        this.context = context;
        this.categories = new ArrayList<>();
        this.arrayAdapter = new ArrayAdapter<String>(context, R.layout.spinner_category_text);
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        ImageLoader.getInstance().init(new UniversalImageLoader(context).getConfig());
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_product;
        private Spinner s_categories;

        public ViewHolder(View itemView) {
            super(itemView);

            this.img_product = itemView.findViewById(R.id.img_product);
            this.s_categories = itemView.findViewById(R.id.s_category);

            this.s_categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (s_categories.getSelectedItemPosition() != 0) {
                        s_categories.setSelection(0);
                        Intent i = new Intent(context, SubCategoryActivity.class);
                        context.startActivity(i);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }

        public void bind (Category category) {
            this.s_categories.setAdapter(arrayAdapter);
            arrayAdapter.addAll(category.getSubCategories());
            arrayAdapter.notifyDataSetChanged();

            for (int i = 0; i < category.getSubCategories().length; i++) {
                Log.d(TAG, "bind: " + category.getSubCategories()[i]);
            }
        }

    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(this.context).inflate(R.layout.card_category, parent, false);
        return new CategoryAdapter.ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        holder.bind(this.categories.get(position));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

}
