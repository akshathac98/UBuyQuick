package com.ubuyquick.customer.shop;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ubuyquick.customer.R;
import com.ubuyquick.customer.SubCategoryActivity;
import com.ubuyquick.customer.adapter.AdAdapter;
import com.ubuyquick.customer.adapter.CategoryAdapter;
import com.ubuyquick.customer.adapter.CategoryListAdapter;
import com.ubuyquick.customer.adapter.MainSearchAdapter3;
import com.ubuyquick.customer.adapter.ShopProductAdapter;
import com.ubuyquick.customer.model.Ad;
import com.ubuyquick.customer.model.Category;
import com.ubuyquick.customer.model.MainSearchProduct;
import com.ubuyquick.customer.model.ShopProduct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Credentials;

public class InventoryFragment extends Fragment {

    private static final String TAG = "InventoryFragment";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private boolean quick_delivery;

    private List<Ad> ads;

    private Spinner s_categories, s_sub_categories;
    private String category, sub_category, shop_id, shop_vendor;
    private ArrayAdapter<String> arrayAdapter;

    private AdAdapter adAdapter;
    private CategoryAdapter categoryAdapter;
    private ArrayAdapter<String> arrayAdapter2;

    private String[] categorys = {"dry_fruits", "Organic Staples"};
    private RecyclerView rv_products, rv_ads, rv_categories;
    private ShopProductAdapter shopProductAdapter;
    private List<ShopProduct> shopProducts;
    private List<Category> categories;

    private ArrayAdapter<String> catAdapter1;
    private ArrayAdapter<String> catAdapter2;
    private ArrayAdapter<String> catAdapter3;
    private Spinner s_category1, s_category2, s_category3;

    private AutoCompleteTextView et_search;
    private MainSearchAdapter3 searchAdapter;
    private List<MainSearchProduct> searchProducts;

    private ExpandableListView list_categories;
    private CategoryListAdapter categoryListAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    private Map<String, ArrayList<String>> subsubcategories;


    private ArrayList<String> juices;
    private ArrayList<String> teacoffee;
    private ArrayList<String> healthenergydrinks;

    private ArrayList<String> laundrydetergents;
    private ArrayList<String> dishwashers;
    private ArrayList<String> cleaners;
    private ArrayList<String> repellents;
    private ArrayList<String> poojaneeds;
    private ArrayList<String> otherneeds;

    private ArrayList<String> bathbody;
    private ArrayList<String> haircare;
    private ArrayList<String> skincare;
    private ArrayList<String> oralcare;
    private ArrayList<String> facecare;
    private ArrayList<String> femininehygeine;
    private ArrayList<String> shavingneeds;
    private ArrayList<String> healthwellness;


    private ArrayList<String> milkproducts;
    private ArrayList<String> jamshoneyspreads;
    private ArrayList<String> buttercheese;
    private ArrayList<String> breakfastcereal;
    private ArrayList<String> breakfastmixes;

    private ArrayList<String> biscuitscookies;
    private ArrayList<String> namkeensnacks;
    private ArrayList<String> chipscrisps;
    private ArrayList<String> chocolatecandies;
    private ArrayList<String> sweets;

    private ArrayList<String> noodlesvermicelli;
    private ArrayList<String> saucesketchups;
    private ArrayList<String> pastasoups;
    private ArrayList<String> readymademealsmixes;
    private ArrayList<String> pickleschutneys;
    private ArrayList<String> cannedfrozenfood;
    private ArrayList<String> bakingdessertitems;
    private ArrayList<String> cakesandpastries;


    private ArrayList<String> babyskinhaircare;

    private ArrayList<String> groceriesstaples;
    private ArrayList<String> riceothergrains;
    private ArrayList<String> dryfruits;
    private ArrayList<String> edibleoils;
    private ArrayList<String> gheevanaspathi;
    private ArrayList<String> spices;
    private ArrayList<String> saltandsugar;
    private ArrayList<String> fruitsandveg;

    private ArrayList<String> petfood;


    private TextView tv_shop_name, tv_delivery, tv_minimum_order;
    private ImageButton btn_plus;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shop_id = getArguments().getString("shop_id");
        shop_vendor = getArguments().getString("shop_vendor");
        quick_delivery = getArguments().getBoolean("quick_delivery");

        adAdapter = new AdAdapter(getContext(), ads);

        categoryAdapter = new CategoryAdapter(getContext());
        categories = new ArrayList<>();

        juices = new ArrayList<>();
        juices.add("Concentrates");
        juices.add("Squash And Sharbat");
        juices.add("Bottles");

        teacoffee = new ArrayList<>();
        teacoffee.add("Tea");
        teacoffee.add("Coffee");

        healthenergydrinks = new ArrayList<>();
        healthenergydrinks.add("Chocolate And Health");

        laundrydetergents = new ArrayList<>();
        laundrydetergents.add("Detergent Powders");
        laundrydetergents.add("Liquid Detergents");
        laundrydetergents.add("Detergent Bars");
        laundrydetergents.add("Laundry Additive");

        dishwashers = new ArrayList<>();
        dishwashers.add("Dishwashing Bars");
        dishwashers.add("Dishwashing Gels");
        dishwashers.add("Dishwashing Powder");

        cleaners = new ArrayList<>();
        cleaners.add("Toilet Cleaner");
        cleaners.add("Floor Cleaner");
        cleaners.add("Multipurpose Cleaners");

        repellents = new ArrayList<>();
        repellents.add("Mosquito Repellents");
        repellents.add("Sprays");
        repellents.add("Creams And Other Repellents");

        poojaneeds = new ArrayList<>();
        poojaneeds.add("Incense Sticks");
        poojaneeds.add("Pooja Oils");

        otherneeds = new ArrayList<>();
        otherneeds.add("Batteries");
        otherneeds.add("Trash Bags");


        bathbody = new ArrayList<>();
        bathbody.add("Body Wash");
        bathbody.add("Soaps");
        bathbody.add("Handwash");

        haircare = new ArrayList<>();
        haircare.add("Hair Oil And Others");
        haircare.add("Hair Colour");
        haircare.add("Shampoo");
        haircare.add("Conditioner");

        skincare = new ArrayList<>();
        skincare.add("Body Lotions");
        skincare.add("Talc Powder");

        oralcare = new ArrayList<>();
        oralcare.add("Toothpaste");
        oralcare.add("Toothbrush");
        oralcare.add("Mouthwash And Others");

        facecare = new ArrayList<>();
        facecare.add("Face Wash");
        facecare.add("Face Cream");
        facecare.add("More Face Products");

        femininehygeine = new ArrayList<>();
        femininehygeine.add("Sanitary Care");
        femininehygeine.add("Hair Removal");

        shavingneeds = new ArrayList<>();
        shavingneeds.add("Razors");
        shavingneeds.add("Cartridges");
        shavingneeds.add("Pre Shave");
        shavingneeds.add("After Shave");

        healthwellness = new ArrayList<>();
        healthwellness.add("Antiseptics");
        healthwellness.add("Other Otcs");
        healthwellness.add("Health Supplements");


        milkproducts = new ArrayList<>();
        milkproducts.add("Lassi And Flavored Milk And Milk Shakes");
        milkproducts.add("Cream And Whitener");

        buttercheese = new ArrayList<>();
        buttercheese.add("Butter");
        buttercheese.add("Cheese");

        breakfastcereal = new ArrayList<>();
        breakfastcereal.add("Flakes");
        breakfastcereal.add("Oats");
        breakfastcereal.add("Muesli");

        breakfastmixes = new ArrayList<>();
        breakfastmixes.add("Breakfast Mixes");

        jamshoneyspreads = new ArrayList<>();
        jamshoneyspreads.add("Honey");
        jamshoneyspreads.add("Jams And Marmalades");
        jamshoneyspreads.add("Food Spreads");
        jamshoneyspreads.add("Peanut Butter And Mayonnaise");
        jamshoneyspreads.add("Dressing And Dips");


        biscuitscookies = new ArrayList<>();
        biscuitscookies.add("Healthy And Digestive");
        biscuitscookies.add("Cream Biscuits Cookies And Wafers");
        biscuitscookies.add("Sweet And Salty");
        biscuitscookies.add("Glucose And Marie");

        namkeensnacks = new ArrayList<>();
        namkeensnacks.add("Bhujia And Sev");
        namkeensnacks.add("Namkeen And Mixtures");
        namkeensnacks.add("Papad And Fryums");

        chipscrisps = new ArrayList<>();
        chipscrisps.add("Crisps And Puffs");
        chipscrisps.add("Popcorns");

        chocolatecandies = new ArrayList<>();
        chocolatecandies.add("Chocolates");
        chocolatecandies.add("Candies");

        sweets = new ArrayList<>();
        sweets.add("Instant Mixes");
        sweets.add("Sweets");


        noodlesvermicelli = new ArrayList<>();
        noodlesvermicelli.add("Instant Noodles");
        noodlesvermicelli.add("Vermicelli");

        saucesketchups = new ArrayList<>();
        saucesketchups.add("Tomato Ketchup And Sauces");
        saucesketchups.add("Soya And Chilli Sauces");

        pastasoups = new ArrayList<>();
        pastasoups.add("Pasta");
        pastasoups.add("Soup");

        readymademealsmixes = new ArrayList<>();
        readymademealsmixes.add("Masala Mixes");

        pickleschutneys = new ArrayList<>();
        pickleschutneys.add("Mango And Lime Pickles");
        pickleschutneys.add("Other Pickles And Chutneys");

        cannedfrozenfood = new ArrayList<>();
        cannedfrozenfood.add("Canned Foods");

        bakingdessertitems = new ArrayList<>();
        bakingdessertitems.add("Baking Ingredients");
        bakingdessertitems.add("Baking Soda");

        cakesandpastries = new ArrayList<>();
        cakesandpastries.add("Cakes");

        babyskinhaircare = new ArrayList<>();
        babyskinhaircare.add("Bathing Needs");
        babyskinhaircare.add("Body Care And Others");

        groceriesstaples = new ArrayList<String>();
        groceriesstaples.add("Atta And Other Flours");
        groceriesstaples.add("Moong And Toor Dal");
        groceriesstaples.add("Urad And Channa Dal");
        groceriesstaples.add("Other Pulses");

        riceothergrains = new ArrayList<>();
        riceothergrains.add("Basmati");
        riceothergrains.add("Sonamasuri And Kolam");
        riceothergrains.add("Other Rice");
        riceothergrains.add("Poha");
        riceothergrains.add("Daliya");

        dryfruits = new ArrayList<>();
        dryfruits.add("Almonds And Cashews");
        dryfruits.add("Dates");
        dryfruits.add("Other Dry Fruits");
        dryfruits.add("Nuts And Seeds");

        edibleoils = new ArrayList<>();
        edibleoils.add("Sunflower Oils");
        edibleoils.add("Mustard Oils");
        edibleoils.add("Health Oils");
        edibleoils.add("Olive Oils");
        edibleoils.add("Rice Bran Oil And Others");

        gheevanaspathi = new ArrayList<>();
        gheevanaspathi.add("Ghee");
        gheevanaspathi.add("Vanaspathi");

        spices = new ArrayList<>();
        spices.add("Whole Spices");
        spices.add("Powdered Spices");
        spices.add("Ready Masala");

        saltandsugar = new ArrayList<>();
        saltandsugar.add("Jaggery And Others");
        saltandsugar.add("Salt");
        saltandsugar.add("Sugar");

        fruitsandveg = new ArrayList<>();
        fruitsandveg.add("Fruits");
        fruitsandveg.add("Vegetables");


        petfood = new ArrayList<>();
        petfood.add("Pigeon Food");

        ArrayList<String> diwali = new ArrayList<>();
        diwali.add("Haldirams Diwali Gift Packs");

        subsubcategories = new HashMap<>();

        subsubcategories.put("Juice And Concentrates", juices);
        subsubcategories.put("Tea And Coffee", teacoffee);
        subsubcategories.put("Health And Energy Drinks", healthenergydrinks);

        subsubcategories.put("Laundry Detergents", laundrydetergents);
        subsubcategories.put("Dishwashers", dishwashers);
        subsubcategories.put("Cleaners", cleaners);
        subsubcategories.put("Repellents", repellents);
        subsubcategories.put("Pooja Needs", poojaneeds);
        subsubcategories.put("Other Needs", otherneeds);


        subsubcategories.put("Bath And Body", bathbody);
        subsubcategories.put("Haircare", haircare);
        subsubcategories.put("Skin Care", skincare);
        subsubcategories.put("Oral Care", oralcare);
        subsubcategories.put("Face Care", facecare);
        subsubcategories.put("Feminine Hygiene", femininehygeine);
        subsubcategories.put("Shaving Needs", shavingneeds);
        subsubcategories.put("Health And Wellness", healthwellness);


        subsubcategories.put("Milk And Milk Products", milkproducts);
        subsubcategories.put("Butter And Cheese", buttercheese);
        subsubcategories.put("Breakfast Cereal", breakfastcereal);
        subsubcategories.put("Breakfast Mixes And Batter", breakfastmixes);
        subsubcategories.put("Jams Honey And Spreads", jamshoneyspreads);

        subsubcategories.put("Biscuits And Cookies", biscuitscookies);
        subsubcategories.put("Namkeen And Snacks", namkeensnacks);
        subsubcategories.put("Chips And Crisps", chipscrisps);
        subsubcategories.put("Chocolate And Candies", chocolatecandies);
        subsubcategories.put("Sweets", sweets);

        subsubcategories.put("Noodles And Vermicelli", noodlesvermicelli);
        subsubcategories.put("Sauces And Ketchups", saucesketchups);
        subsubcategories.put("Pasta And Soups", pastasoups);
        subsubcategories.put("Ready Made Meals And Mixes", readymademealsmixes);
        subsubcategories.put("Pickles And Chutneys", pickleschutneys);
        subsubcategories.put("Canned And Frozen Food", cannedfrozenfood);
        subsubcategories.put("Baking And Dessert Items", bakingdessertitems);
        subsubcategories.put("Cakes And Pastries", cakesandpastries);

        subsubcategories.put("Baby Skin And Hair Care", babyskinhaircare);

        subsubcategories.put("Staples", groceriesstaples);
        subsubcategories.put("Rice And Other Grains", riceothergrains);
        subsubcategories.put("Dry Fruits", dryfruits);
        subsubcategories.put("Edible Oils", edibleoils);
        subsubcategories.put("Ghee And Vanaspathi", gheevanaspathi);
        subsubcategories.put("Spices", spices);
        subsubcategories.put("Salt And Sugar", saltandsugar);
        subsubcategories.put("Fruits And Vegetables", fruitsandveg);

        subsubcategories.put("Pet Food", petfood);

        subsubcategories.put("Diwali Gift Packs", diwali);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_inventory, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        list_categories = (ExpandableListView) view.findViewById(R.id.list_category);
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Grocery And Staples");
        listDataHeader.add("Beverages");
        listDataHeader.add("Household Needs");
        listDataHeader.add("Personal Care");
        listDataHeader.add("Breakfast And Dairy");
        listDataHeader.add("Biscuits Snacks And Chocolates");
        listDataHeader.add("Noodles Sauces And Instant Food");
        listDataHeader.add("Baby And Kids");
        listDataHeader.add("Pet Supplies");

        // Adding child data
        List<String> beverages = new ArrayList<String>();

        beverages.add("Health And Energy Drinks");
        beverages.add("Tea And Coffee");
        beverages.add("Juice And Concentrates");

        List<String> household = new ArrayList<String>();
        household.add("Laundry Detergents");
        household.add("Dishwashers");
        household.add("Cleaners");
        household.add("Repellents");
        household.add("Pooja Needs");
        household.add("Other Needs");

        List<String> personal = new ArrayList<>();
        personal.add("Bath And Body");
        personal.add("Haircare");
        personal.add("Skin Care");
        personal.add("Oral Care");
        personal.add("Face Care");
        personal.add("Feminine Hygiene");
        personal.add("Shaving Needs");
        personal.add("Health And Wellness");


        List<String> breakfast = new ArrayList<>();
        breakfast.add("Milk And Milk Products");
        breakfast.add("Butter And Cheese");
        breakfast.add("Breakfast Cereal");
        breakfast.add("Jams Honey And Spreads");
        breakfast.add("Breakfast Mixes And Batter");

        List<String> biscuits = new ArrayList<>();
        biscuits.add("Biscuits And Cookies");
        biscuits.add("Namkeen And Snacks");
        biscuits.add("Chips And Crisps");
        biscuits.add("Chocolate And Candies");
        biscuits.add("Sweets");

        List<String> noodles = new ArrayList<>();
        noodles.add("Noodles And Vermicelli");
        noodles.add("Sauces And Ketchups");
        noodles.add("Pasta And Soups");
        noodles.add("Ready Made Meals And Mixes");
        noodles.add("Pickles And Chutneys");
        noodles.add("Canned And Frozen Food");
        noodles.add("Baking And Dessert Items");
        noodles.add("Cakes And Pastries");

        List<String> baby = new ArrayList<>();
        baby.add("Baby Skin And Hair Care");

        List<String> pet = new ArrayList<>();
        pet.add("Pet Food");

        final List<String> staples = new ArrayList<>();
        staples.add("Staples");
        staples.add("Rice And Other Grains");
        staples.add("Dry Fruits");
        staples.add("Edible Oils");
        staples.add("Ghee And Vanaspathi");
        staples.add("Spices");
        staples.add("Salt And Sugar");
        staples.add("Fruits And Vegetables");
        staples.add("Diwali Gift Packs");

        listDataChild.put(listDataHeader.get(0), staples);
        listDataChild.put(listDataHeader.get(1), beverages);
        listDataChild.put(listDataHeader.get(2), household);
        listDataChild.put(listDataHeader.get(3), personal);
        listDataChild.put(listDataHeader.get(4), breakfast);
        listDataChild.put(listDataHeader.get(5), biscuits);
        listDataChild.put(listDataHeader.get(6), noodles);
        listDataChild.put(listDataHeader.get(7), baby);
        listDataChild.put(listDataHeader.get(8), pet);

        categoryListAdapter = new CategoryListAdapter(getContext(), listDataHeader, listDataChild);

        list_categories.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
//                Toast.makeText(
//                        getContext(),
//                        listDataHeader.get(groupPosition)
//                                + " : "
//                                + listDataChild.get(
//                                listDataHeader.get(groupPosition)).get(childPosition), Toast.LENGTH_SHORT)
//                        .show();
                Intent i = new Intent(getContext(), SubCategoryActivity.class);
                i.putExtra("shop_id", shop_id);
                i.putExtra("sub_category", listDataChild.get(
                        listDataHeader.get(groupPosition)).get(childPosition));
                i.putStringArrayListExtra("list", subsubcategories.get(listDataChild.get(
                        listDataHeader.get(groupPosition)).get(childPosition)));
                startActivity(i);
                return false;
            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        list_categories.setIndicatorBounds(width - 50, width);

        LayoutInflater layoutInflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.inventory_header, list_categories, false);
        tv_delivery = header.findViewById(R.id.tv_delivery);

        if (quick_delivery) {
            tv_delivery.setVisibility(View.VISIBLE);
        }

        tv_shop_name = header.findViewById(R.id.tv_shop_name);
        btn_plus = header.findViewById(R.id.btn_plus);
        tv_minimum_order = header.findViewById(R.id.tv_minimum_order);

        ads = new ArrayList<>();
        ads.add(new Ad(R.mipmap.haldiram_ad));
        ads.add(new Ad(R.mipmap.kellogs_ad));
        ads.add(new Ad(R.mipmap.nandhini_ad));
        ads.add(new Ad(R.mipmap.oil_ad));

        rv_ads = header.findViewById(R.id.rv_ads);
        adAdapter.setAds(ads);
        rv_ads.setAdapter(adAdapter);


        list_categories.addHeaderView(header);

        // setting list adapter
        list_categories.setAdapter(categoryListAdapter);

        tv_shop_name.setText(getArguments().getString("shop_name"));
        tv_minimum_order.setText("Min Order : \u20B9" + getArguments().getDouble("minimum_order", 0.0));

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
                searchAdapter = new MainSearchAdapter3(getContext(), 1, searchProducts, shop_id, et_search);

                et_search.setAdapter(searchAdapter);

                HashMap<String, String> headerMap = new HashMap<>();
                headerMap.put("Authorization", Credentials.basic(getString(R.string.elastic_user), getString(R.string.elastic_password)));

                HashMap<String, String> queryMap = new HashMap<>();
                queryMap.put("q", "Products:* " + s.toString() + "*");
                queryMap.put("from", "0");
                queryMap.put("size", "200");

                AndroidNetworking.get(getString(R.string.elastic_host) + "/" + getString(R.string.elastic_index) + "/_search")
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
                                            .collection("shop_lists").document(shop_id).collection("lists").add(product);
                                    Toast.makeText(getContext(), "Added " + Integer.parseInt(et_quantity.getText().toString())
                                            + " " + et_search.getText().toString(), Toast.LENGTH_SHORT).show();
                                    et_search.setText("");
                                }
                            }
                        });
                builder.show();
            }
        });

/*

        s_category1 = view.findViewById(R.id.s_category1);
        s_category1.setAdapter(catAdapter1);
        s_category1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (s_category1.getSelectedItemPosition() != 0) {
                    s_category1.setSelection(0);
                    Intent i = new Intent(getContext(), SubCategoryActivity.class);
                    getContext().startActivity(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        s_category2 = view.findViewById(R.id.s_category2);
        s_category2.setAdapter(catAdapter2);
        s_category2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (s_category2.getSelectedItemPosition() != 0) {
                    s_category2.setSelection(0);
                    Intent i = new Intent(getContext(), SubCategoryActivity.class);
                    getContext().startActivity(i);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        s_category3 = view.findViewById(R.id.s_category3);
        s_category3.setAdapter(catAdapter3);
        s_category3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (s_category3.getSelectedItemPosition() != 0) {
                    s_category3.setSelection(0);
                    Intent i = new Intent(getContext(), SubCategoryActivity.class);
                    getContext().startActivity(i);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
*/

        return view;
    }
}