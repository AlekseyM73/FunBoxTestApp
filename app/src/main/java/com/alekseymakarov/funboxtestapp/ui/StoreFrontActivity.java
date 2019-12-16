package com.alekseymakarov.funboxtestapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.widget.ViewPager2;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.alekseymakarov.funboxtestapp.R;
import com.alekseymakarov.funboxtestapp.model.Product;
import com.alekseymakarov.funboxtestapp.utils.EventHelper;
import com.alekseymakarov.funboxtestapp.utils.InitialDataParser;
import com.alekseymakarov.funboxtestapp.viewmodel.StoreFrontViewModel;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class StoreFrontActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private StoreFrontViewModel storeFrontViewModel;
    private StoreFrontViewPagerAdapter adapter;

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

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

    @Override
    protected void onRestart() {
        super.onRestart();
        loadProductsInAdapter();
    }

    private void setViews() {
        Button buttonBuy = findViewById(R.id.viewPagerButtonBuy);
        Button buttonStoreFront = findViewById(R.id.viewPagerButtonStoreFront);
        Button buttonBackEnd = findViewById(R.id.viewPagerButtonBackEnd);
        buttonStoreFront.setEnabled(false);

        buttonBuy.setOnClickListener(buttonBuyListener);
        buttonBackEnd.setOnClickListener(buttonBackEndListener);
    }

    private void loadProductsInAdapter() {
        storeFrontViewModel.getProducts().observe(this, products -> {
            adapter.setProducts(products);
        });

    }
    @Subscribe
    public void onSaveAfterEditProductFinished (EventHelper eventHelper){
        loadProductsInAdapter();
    }

    private void checkForFirstStartApp() {
        SharedPreferences sharedPreferences = getSharedPreferences
                ("com.alekseymakarov.funboxtestapp", MODE_PRIVATE);
        // Check first run if true load data from data.csv
        // else load data from db
        String IS_FIRST_RUN = "isFirstRun";

        if (sharedPreferences.getBoolean(IS_FIRST_RUN, true)) {
            sharedPreferences.edit().putBoolean(IS_FIRST_RUN, false).apply();

            Observable<List<Product>> observable = InitialDataParser
                    .readFromCsvOrFallbackToJson(this);
            List<Product> productList = new ArrayList<>();
            observable.subscribe(list->{
                productList.addAll(list);
            });
            storeFrontViewModel.saveProducts(productList
                    , new OnSaveProductsFromFileToDBListener() {
                        @Override
                        public void onSaveFromFileToDBFinished() {
                            loadProductsInAdapter();
                        }
                    });
        } else {
            loadProductsInAdapter();
        }
    }

    View.OnClickListener buttonBuyListener = v -> {
        if (adapter.getProductFromAdapter(viewPager.getCurrentItem()) != null) {
            Product prod = adapter.getProductFromAdapter(viewPager.getCurrentItem());
            int currentQuantity = prod.getQuantity();
            if (currentQuantity > 0) {
                prod.setQuantity(currentQuantity - 1);
                storeFrontViewModel.updateProductQuantity(prod, new OnUpdateProductQuantityToDBListener() {
                    @Override
                    public void onUpdateProductQuantityToDBFinished() {
                        loadProductsInAdapter();
                    }
                });
            }
        }
    };

    View.OnClickListener buttonBackEndListener = v -> {
        Intent intent = new Intent(this, BackEndActivity.class);
        intent.addFlags(FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    };

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}



