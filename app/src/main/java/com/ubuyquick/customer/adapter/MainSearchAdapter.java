package com.ubuyquick.customer.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;
import com.ubuyquick.customer.DeliveryOptionsActivity;
import com.ubuyquick.customer.R;
import com.ubuyquick.customer.model.MainSearchProduct;
import com.ubuyquick.customer.model.NewListProduct;
import com.ubuyquick.customer.utils.UniversalImageLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainSearchAdapter extends ArrayAdapter {

    private List<MainSearchProduct> products;
    private Context context;
    private int itemLayout;
    private NewListProductAdapter newListProductAdapter;
    private List<NewListProduct> newListProducts;
    private AutoCompleteTextView clear_field;

    public MainSearchAdapter(@NonNull Context context, int resource, List<MainSearchProduct> products, NewListProductAdapter
            newListProductAdapter, List<NewListProduct> newListProducts, AutoCompleteTextView clear_field) {
        super(context, resource, products);
        this.products = products;
        this.clear_field = clear_field;
        this.newListProductAdapter = newListProductAdapter;
        this.newListProducts = newListProducts;
        this.context = context;
        ImageLoader.getInstance().init(new UniversalImageLoader(context).getConfig());

        this.itemLayout = resource;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_search, parent, false);

        TextView tv_product_name = convertView.findViewById(R.id.tv_product_name);
        TextView tv_product_mrp = convertView.findViewById(R.id.tv_product_mrp);
        TextView tv_product_measure = convertView.findViewById(R.id.tv_product_measure);
        //TODO 1
        ImageView tv_product_image = convertView.findViewById(R.id.tv_product_image);

        tv_product_name.setText(getItem(position).getProductName());
        tv_product_mrp.setText("MRP: \u20B9" + getItem(position).getProductMrp());
        tv_product_measure.setText(getItem(position).getProductMeasure());
        //TODO 1.2
//        Picasso.get()//.load(url)
//                .load(getItem(position).getProductUrl())
//                .placeholder(android.R.color.white).error(android.R.color.white).into(tv_product_image);

        UniversalImageLoader.setImage(getItem(position).getProductUrl(), tv_product_image);


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final MainSearchProduct searchProduct = products.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                View viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_product_details, null, false);
                final TextView tv_name = (TextView) viewInflated.findViewById(R.id.tv_name);
                final TextView tv_mrp = (TextView) viewInflated.findViewById(R.id.tv_price);
                final TextView tv_measure = (TextView) viewInflated.findViewById(R.id.tv_measure);
                final EditText et_quantity = (EditText) viewInflated.findViewById(R.id.et_quantity);
                //TODO 1.1
                final ImageView tv_image = (ImageView) viewInflated.findViewById(R.id.tv_image);

                tv_name.setText(searchProduct.getProductName());
                tv_measure.setText(searchProduct.getProductMeasure());
                tv_mrp.setText("\u20B9" + searchProduct.getProductMrp());

                builder.setView(viewInflated);
                builder.setMessage("Enter Product Quantity")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (et_quantity.getText().toString().isEmpty())
                                    Toast.makeText(context, "Please enter quantity to add", Toast.LENGTH_SHORT).show();
                                else {
                                    newListProducts.add(new NewListProduct(searchProduct.getProductName(),
                                            Integer.parseInt(et_quantity.getText().toString())
                                            , "1", searchProduct.getProductMrp(), searchProduct.getProductMeasure()));
                                    newListProductAdapter.setNewListProducts(newListProducts);
                                }
                                clear_field.setText("");
                            }
                        });
                builder.show();
//
//                Map<String, Object> product = new HashMap<>();
//                product.put("product_name", searchProduct.getProductName());
//                product.put("product_name", searchProduct.getProductName());
//                product.put("product_name", searchProduct.getProductName());

            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Nullable
    @Override
    public MainSearchProduct getItem(int position) {
        return products.get(position);
    }
}
