package com.alekseymakarov.funboxtestapp.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alekseymakarov.funboxtestapp.R;
import com.alekseymakarov.funboxtestapp.viewmodel.BackEndViewModel;

public class BackEndActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BackEndViewModel backEndViewModel;
    private BackEndRecyclerViewAdapter adapter;
    private Button buttonStoreFront;
    private Button buttonBackEnd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backend_recycler_view);

        setViews();

        recyclerView = findViewById(R.id.recyclerviewContainer);
        adapter = new BackEndRecyclerViewAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        backEndViewModel = ViewModelProviders.of(this).get(BackEndViewModel.class);

        updateView();
    }

    private void updateView() {
        backEndViewModel.getProducts().observe(this, products -> {
            adapter.setProducts(products);
        });
    }

    private void setViews() {
        buttonStoreFront = findViewById(R.id.recyclerViewButtonStoreFront);
        buttonBackEnd = findViewById(R.id.recyclerViewButtonBackEnd);
        buttonBackEnd.setEnabled(false);

        buttonStoreFront.setOnClickListener(buttonStoreFrontListener);
    }

    View.OnClickListener buttonStoreFrontListener = v -> {
        onBackPressed();
    };

}
