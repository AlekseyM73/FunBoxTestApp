package com.alekseymakarov.funboxtestapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.alekseymakarov.funboxtestapp.R;
import com.alekseymakarov.funboxtestapp.utils.EventHelper;
import com.alekseymakarov.funboxtestapp.viewmodel.BackEndViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class BackEndActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BackEndViewModel backEndViewModel;
    private BackEndRecyclerViewAdapter adapter;
    private Button buttonStoreFront;
    private Button buttonBackEnd;
    private MenuItem menuAdd;

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

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

    @Override
    protected void onRestart() {
        super.onRestart();
        updateView();
    }

    private void updateView() {
        backEndViewModel.getProducts().observe(this, products -> {
            adapter.setProducts(products);
        });
    }
    @Subscribe
    public void onSaveAfterEditProductFinished (EventHelper eventHelper){
        updateView();
    }

    private void setViews() {
        Toolbar toolbar = findViewById(R.id.recyclerviewBackEndActivityToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        buttonStoreFront = findViewById(R.id.recyclerViewButtonStoreFront);
        buttonBackEnd = findViewById(R.id.recyclerViewButtonBackEnd);
        buttonBackEnd.setEnabled(false);

        buttonStoreFront.setOnClickListener(buttonStoreFrontListener);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_backend_add_menu, menu);
        menuAdd = menu.findItem(R.id.recyclerview_menu_add);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.recyclerview_menu_add: {
                Intent intent = new Intent(this
                        , BackEndActivityNewProduct.class);
                startActivity(intent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    View.OnClickListener buttonStoreFrontListener = v -> {
        onBackPressed();
    };

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
