package com.ubuyquick.customer;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;

public class UBuyQuickApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AndroidNetworking.initialize(getApplicationContext());
    }
}
