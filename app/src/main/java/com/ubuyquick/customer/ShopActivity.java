package com.ubuyquick.customer;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ubuyquick.customer.shop.InventoryFragment;
import com.ubuyquick.customer.shop.ShopListFragment;

public class ShopActivity extends AppCompatActivity {

    private static final String TAG = "ShopActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String shop_id;
    private String shop_vendor;
    private String shop_name;
    private double minimum_order;
    private boolean quick_delivery = false;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        Intent i = getIntent();
        shop_id = i.getStringExtra("SHOP_ID");
        shop_vendor = i.getStringExtra("SHOP_VENDOR");
        shop_name = i.getStringExtra("SHOP_NAME");
        minimum_order = i.getDoubleExtra("MINIMUM_ORDER", 0.0);
        quick_delivery = i.getBooleanExtra("QUICK_DELIVERY", false);


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        db.collection("customers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3)).collection("shop_lists")
                .document(shop_id).collection("lists").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        tabLayout.getTabAt(1).setText("Shopping List(" + task.getResult().getDocuments().size() + ")");
                    }
                });

//        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_up);
//        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        this.getSupportActionBar().setElevation(0.0f);
//        this.getSupportActionBar().setTitle(i.getStringExtra("SHOP_NAME"));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shop, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            super.onBackPressed();
        }
        return false;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    Fragment inventoryFragment = new InventoryFragment();
                    Bundle inventoryArgs = new Bundle();
                    inventoryArgs.putString("shop_id", shop_id);
                    inventoryArgs.putString("shop_vendor", shop_vendor);
                    inventoryArgs.putString("shop_name", shop_name);
                    inventoryArgs.putDouble("minimum_order", minimum_order);
                    inventoryArgs.putBoolean("quick_delivery", quick_delivery);
                    inventoryFragment.setArguments(inventoryArgs);
                    return inventoryFragment;
                default:
                    Fragment shopListFragment = new ShopListFragment();
                    Bundle shopArgs = new Bundle();
                    shopArgs.putString("shop_id", shop_id);
                    shopArgs.putString("shop_vendor", shop_vendor);
                    shopListFragment.setArguments(shopArgs);
                    return shopListFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
