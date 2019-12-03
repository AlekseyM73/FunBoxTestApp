package com.alekseymakarov.funboxtestapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.widget.ViewPager2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.alekseymakarov.funboxtestapp.R;
import com.alekseymakarov.funboxtestapp.model.Product;
import com.alekseymakarov.funboxtestapp.utils.InitialDataParser;
import com.alekseymakarov.funboxtestapp.viewmodel.StoreFrontViewModel;

public class StoreFrontActivity extends AppCompatActivity implements
        OnSaveProductsFromFileToDBListener, OnUpdateProductQuantityToDBListener {

    private ViewPager2 viewPager;
    private StoreFrontViewModel storeFrontViewModel;
    private StoreFrontViewPagerAdapter adapter;

    private Button buttonBuy;
    private Button buttonStoreFront;
    private Button buttonBackEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storefront);

        setViews();

        viewPager = findViewById(R.id.activityStoreFrontViewPager);
        storeFrontViewModel = ViewModelProviders.of(this)
                .get(StoreFrontViewModel.class);
        adapter = new StoreFrontViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        checkForFirstStartApp();
    }

    private void setViews() {
        buttonBuy = findViewById(R.id.viewPagerButtonBuy);
        buttonStoreFront = findViewById(R.id.viewPagerButtonStoreFront);
        buttonBackEnd = findViewById(R.id.viewPagerButtonBackEnd);
        buttonStoreFront.setEnabled(false);

        buttonBuy.setOnClickListener(buttonBuyListener);
        buttonStoreFront.setOnClickListener(buttonStoreFrontListener);
        buttonBackEnd.setOnClickListener(buttonBackEndListener);
    }

    private void loadProductsInAdapter() {
        storeFrontViewModel.getProducts().observe(this, products -> {
           /*for (int i = 0; i<products.size();i++){
                if (products.get(i).getQuantity()==0){
                    products.remove(products.get(i));
                }
            }*/
            adapter.setProducts(products);
        });

    }

    private void checkForFirstStartApp() {
        SharedPreferences sharedPreferences = getSharedPreferences
                ("com.alekseymakarov.funboxtestapp", MODE_PRIVATE);
        // Check first run if true load data from data.csv
        // else load data from db
        String IS_FIRST_RUN = "isFirstRun";

        if (sharedPreferences.getBoolean(IS_FIRST_RUN, true)) {
            sharedPreferences.edit().putBoolean(IS_FIRST_RUN, false).commit();
            storeFrontViewModel.saveProducts
                    (InitialDataParser.readInitialProductData(this), this);
        } else {
            loadProductsInAdapter();
        }
    }

    View.OnClickListener buttonBuyListener = v -> {
        Product prod = adapter.getProductFromAdapter(viewPager.getCurrentItem());
        int currentQuantity = prod.getQuantity();
        if (currentQuantity > 0) {
            prod.setQuantity(currentQuantity - 1);
            storeFrontViewModel.updateProductQuantity(prod, this);
        }

    };

    View.OnClickListener buttonStoreFrontListener = v -> {

    };

    View.OnClickListener buttonBackEndListener = v -> {

    };

    public void onSaveFromFileToDBFinished() {
        loadProductsInAdapter();
    }

    public void onUpdateProductQuantityToDBFinished() {
        loadProductsInAdapter();
    }

}



