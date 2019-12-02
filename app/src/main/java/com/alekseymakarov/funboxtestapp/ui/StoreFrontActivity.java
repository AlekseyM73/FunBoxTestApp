package com.alekseymakarov.funboxtestapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.alekseymakarov.funboxtestapp.R;
import com.alekseymakarov.funboxtestapp.utils.InitialDataParser;

public class StoreFrontActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storefront);

       // InitialDataParser.readInitialProductData(this);
    }
}
