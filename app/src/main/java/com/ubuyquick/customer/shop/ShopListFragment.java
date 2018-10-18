package com.ubuyquick.customer.shop;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ubuyquick.customer.CreateListActivity;
import com.ubuyquick.customer.DeliveryOptionsActivity;
import com.ubuyquick.customer.Manifest;
import com.ubuyquick.customer.R;
import com.ubuyquick.customer.adapter.AdAdapter;
import com.ubuyquick.customer.adapter.MainSearchAdapter;
import com.ubuyquick.customer.adapter.MainSearchAdapter2;
import com.ubuyquick.customer.adapter.ShopListAdapter;
import com.ubuyquick.customer.adapter.ShopProductAdapter;
import com.ubuyquick.customer.model.MainSearchProduct;
import com.ubuyquick.customer.model.ShopProduct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Credentials;

public class ShopListFragment extends Fragment {

    private static final String TAG = "ShopListFragment";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String shop_id, shop_vendor;

    private AutoCompleteTextView et_search;

    private RecyclerView rv_list, rv_ads;
    private List<ShopProduct> shopProducts;
    private ShopListAdapter shopListAdapter;
    private Button btn_checkout, btn_save_list;
    private ImageButton btn_plus;

    private MainSearchAdapter2 searchAdapter;
    private List<MainSearchProduct> searchProducts;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUserVisibleHint(false);

        shop_id = getArguments().getString("shop_id");
        shop_vendor = getArguments().getString("shop_vendor");

        shopListAdapter = new ShopListAdapter(getContext(), shop_id);
        shopProducts = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_list, container, false);

        rv_list = view.findViewById(R.id.rv_list);
        rv_list.setAdapter(shopListAdapter);

        btn_checkout = (Button) view.findViewById(R.id.btn_checkout);
        btn_save_list = (Button) view.findViewById(R.id.btn_save);
        btn_plus = (ImageButton) view.findViewById(R.id.btn_plus);

        btn_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), DeliveryOptionsActivity.class);
                i.putExtra("shop_id", getArguments().getString("shop_id"));
                i.putExtra("shop_vendor", getArguments().getString("shop_vendor"));
                i.putExtra("checkout_list", "shop");
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        et_search = (AutoCompleteTextView) view.findViewById(R.id.et_search);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final HashMap<String, Double> priceList = new HashMap<>();
                int layoutItemId = android.R.layout.simple_dropdown_item_1line;
                final List<String> prodList = new ArrayList<>();
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), layoutItemId, prodList);

                final List<MainSearchProduct> searchProducts = new ArrayList<>();
                searchAdapter = new MainSearchAdapter2(getContext(), 1, searchProducts,
                        shopListAdapter, shopProducts, shop_id, et_search);

                et_search.setAdapter(searchAdapter);

                /*
                et_typing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final String selected_product = et_typing.getText().toString();
                        et_typing.setText("");
                        int pos = prodList.indexOf(selected_product);

                        final Map<String, Object> product = new HashMap<>();
                        product.put("product_name", selected_product);
                        product.put("product_quantity", 1);
                        product.put("product_mrp", priceList.get(selected_product));
                        db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3)).collection("current_list")
                                .add(product).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                product.put("product_id", task.getResult().getId());
                                searchProducts.add(new MainSearchProduct(selected_product, Double.parseDouble(product.get("product_mrp").toString())
                                , "Measure", 1));
                                searchAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
*/

                HashMap<String, String> headerMap = new HashMap<>();
                headerMap.put("Authorization", Credentials.basic("elastic", "k0TWsTm4bb59v5JmnbBni27N"));

                HashMap<String, String> queryMap = new HashMap<>();
                queryMap.put("q", "Products:* " + s.toString() + "*");
                queryMap.put("from", "0");
                queryMap.put("size", "50");

                AndroidNetworking.get("https://8ec7da3e09b84f9fabf3785d0ae0cc40.europe-west1.gcp.cloud.es.io:9243/ubq-has/_search")
                        .addQueryParameter(queryMap)
                        .addHeaders(headerMap)
                        .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray productsArray = response.getJSONObject("hits").getJSONArray("hits");

                            if (productsArray.length() == 0) {
                                btn_plus.setVisibility(View.VISIBLE);
                            } else {
                                btn_plus.setVisibility(View.INVISIBLE);
                                for (int i = 0; i < productsArray.length(); i++) {
                                    JSONObject productObj = productsArray.getJSONObject(i);
                                    String product_name = productObj.getJSONObject("_source").getString("Products");
                                    String product_measure = productObj.getJSONObject("_source").getString("Measure");
                                    String image_url = productObj.getJSONObject("_source").getString("Url");
                                    double product_mrp = productObj.getJSONObject("_source").getDouble("Price");

                                    priceList.put(product_name, productObj.getJSONObject("_source").getDouble("Price"));
                                    searchProducts.add(new MainSearchProduct(product_name, product_mrp, product_measure, 1, image_url));
                                }
                                searchAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            Log.d(TAG, "onResponse: exception: " + e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: " + anError.getErrorDetail());
                    }
                });

            }
        });

        btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());

                View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_product_input, null, false);
                final TextView tv_name = (TextView) viewInflated.findViewById(R.id.tv_name);
                final TextView tv_mrp = (TextView) viewInflated.findViewById(R.id.tv_price);
                final EditText et_quantity = (EditText) viewInflated.findViewById(R.id.et_quantity);
                final EditText et_measure = (EditText) viewInflated.findViewById(R.id.et_measure);

                tv_name.setText(et_search.getText().toString());
                tv_mrp.setText(0.0 + "");

                builder.setView(viewInflated);
                builder.setNegativeButton("Cancel", null)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (et_quantity.getText().toString().isEmpty())
                                    Toast.makeText(getContext(), "Please enter quantity to add", Toast.LENGTH_SHORT).show();
                                if (et_measure.getText().toString().isEmpty())
                                    Toast.makeText(getContext(), "Please enter the measure", Toast.LENGTH_SHORT).show();
                                else {
                                    Map<String, Object> product = new HashMap<>();
                                    product.put("product_measure", et_measure.getText().toString());
                                    product.put("product_name", et_search.getText().toString());
                                    product.put("product_quantity", Integer.parseInt(et_quantity.getText().toString()));
                                    product.put("product_mrp", 0.0);
                                    db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                            .collection("shop_lists").document(shop_id).collection("lists").add(product)
                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            shopProducts.add(new ShopProduct(et_search.getText().toString(),
                                                    Integer.parseInt(et_quantity.getText().toString()), task.getResult().getId(),
                                                    0.0, et_measure.getText().toString()));
                                            shopListAdapter.setListProducts(shopProducts);
                                            et_search.setText("");
                                        }
                                    });
                                }
                            }
                        });
                builder.show();
            }
        });

        /*
        btn_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Place order?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        final String timestamp = new Timestamp(System.currentTimeMillis()).getTime() + "";
                                        Map<String, Object> customer = task.getResult().getData();
                                        Map<String, Object> order = new HashMap<>();
                                        order.put("customer_id", customer.get("customer_id").toString());
                                        order.put("customer_name", customer.get("customer_name").toString());
                                        order.put("delivery_address", customer.get("delivery_address").toString());
                                        order.put("order_id", timestamp);
                                        order.put("ordered_at", timestamp);
                                        db.collection("vendors").document(shop_vendor).collection("shops")
                                                .document(shop_id).collection("new_orders").document(timestamp).set(order)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Map<String, Object> product = new HashMap<>();
                                                for (ShopProduct shopProduct : shopProducts) {
                                                    product.put("name", shopProduct.getProductName());
                                                    product.put("product_id", shopProduct.getProductId());
                                                    product.put("mrp", shopProduct.getProductMrp());
                                                    product.put("quantity", shopProduct.getProductQuantity());
                                                    product.put("image_url", shopProduct.getProductImageUrl());
                                                    product.put("available", false);

                                                    db.collection("vendors").document(shop_vendor).collection("shops")
                                                            .document(shop_id).collection("new_orders").document(timestamp)
                                                            .collection("products").document(shopProduct.getProductId())
                                                            .set(product);
                                                }
                                            }
                                        });


                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
            }
        });

        */

        loadList();

        btn_save_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                        .collection("shop_lists").document(shop_id).collection("lists").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                for (DocumentSnapshot document : documents) {
                                    Toast.makeText(getContext(), "Saving please wait...", Toast.LENGTH_SHORT).show();
                                    db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                            .collection("shop_lists").document(shop_id).collection("lists").
                                            document(document.getId()).delete();
                                }

                                for (ShopProduct shopProduct : shopProducts) {
                                    Toast.makeText(getContext(), "Saving please wait...", Toast.LENGTH_SHORT).show();
                                    Map<String, Object> product = new HashMap<>();
                                    product.put("product_measure", shopProduct.getProductMeasure());
                                    product.put("product_id", shopProduct.getProductId());
                                    product.put("product_name", shopProduct.getProductName());
                                    product.put("product_quantity", shopProduct.getProductQuantity());
                                    product.put("product_mrp", shopProduct.getProductMrp());
                                    db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                            .collection("shop_lists").document(shop_id).collection("lists").add(product);
                                }
                                Toast.makeText(getContext(), "List changes saved", Toast.LENGTH_SHORT).show();
                            }
                        });


            }
        });

        return view;
    }

    private void loadList() {
        try {
            shopProducts.clear();
            shopListAdapter.setListProducts(shopProducts);
            db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                    .collection("shop_lists").document(shop_id).collection("lists").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            for (DocumentSnapshot document : documents) {
                                Map<String, Object> product = document.getData();
                                shopProducts.add(new ShopProduct(product.get("product_name").toString(),
                                        Integer.parseInt(product.get("product_quantity").toString()),
                                        document.getId(),
                                        Double.parseDouble(product.get("product_mrp").toString()),
                                        product.get("product_measure").toString()));
                            }
                            shopListAdapter.setListProducts(shopProducts);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            loadList();


        } else {
            try {
                shopProducts.clear();
                shopListAdapter.setListProducts(shopProducts);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
