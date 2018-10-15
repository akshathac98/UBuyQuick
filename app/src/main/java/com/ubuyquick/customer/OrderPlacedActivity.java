package com.ubuyquick.customer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class OrderPlacedActivity extends AppCompatActivity {

    private TextView tv_placed;
    private String shop_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placed);

        shop_name = getIntent().getStringExtra("shop_id").replace("_", " ");

        tv_placed = findViewById(R.id.tv_placed);
        tv_placed.setText(tv_placed.getText() + shop_name);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(OrderPlacedActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}
