package com.alekseymakarov.funboxtestapp.application;

import android.app.Application;

import com.alekseymakarov.funboxtestapp.db.ProductDatabase;

public class App extends Application {

    public static ProductDatabase productDatabase;

    @Override
    public void onCreate() {
        super.onCreate();

        productDatabase = ProductDatabase.getProductDatabase(this);
    }
}
