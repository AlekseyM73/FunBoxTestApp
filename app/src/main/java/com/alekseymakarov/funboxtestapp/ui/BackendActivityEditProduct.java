package com.alekseymakarov.funboxtestapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alekseymakarov.funboxtestapp.R;
import com.alekseymakarov.funboxtestapp.application.App;
import com.alekseymakarov.funboxtestapp.dao.ProductDAO;
import com.alekseymakarov.funboxtestapp.model.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

import static android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE;

public class BackendActivityEditProduct extends AppCompatActivity {

    private final String IS_EDIT_BTN_PRESSED = "isEditBtnPressed";
    private int productId;
    private EditText etName;
    private EditText etPrice;
    private EditText etQuantity;
    private TextInputLayout textInputLayout;
    private FloatingActionButton fab;
    private MenuItem menuOk;
    private MenuItem menuCancel;
    private boolean isEditBtnPressed = false;
    private Disposable subscribe;
    private ProductDAO productDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backend_edit_product);

        productDAO = App.productDatabase.getProductDAO();

        setViews();

        if (savedInstanceState != null) {
            isEditBtnPressed = savedInstanceState.getBoolean(IS_EDIT_BTN_PRESSED);
        }
        Intent intent = getIntent();
        productId = intent.getIntExtra(BackEndRecyclerViewAdapter.PRODUCT_ID, -1);

        if (!isEditBtnPressed) {
            getProduct();
        }
    }

    private void setViews() {
        etName = findViewById(R.id.editProductActivityNameEditText);
        etPrice = findViewById(R.id.editProductActivityPriceEditText);
        etQuantity = findViewById(R.id.editProductActivityQuantityEditText);
        textInputLayout = findViewById(R.id.editProductActivityTitleTextInputLayout);
        Toolbar toolbar = findViewById(R.id.editProductActivityToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.edit_product_fab);
        fab.setOnClickListener(fabListener);

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayout.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_EDIT_BTN_PRESSED, isEditBtnPressed);
    }

    View.OnClickListener fabListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setViewsAsEditable();
            isEditBtnPressed = true;
        }
    };

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_backend_edit_product_menu, menu);
        menuOk = menu.findItem(R.id.edit_product_menu_ok);
        menuCancel = menu.findItem(R.id.edit_product_menu_cancel);

        if (isEditBtnPressed) {
            setViewsAsEditable();
        } else {
            setViewsAsText();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_product_menu_ok: {
                if (!etName.getText().toString().isEmpty()) {
                    updateProduct();
                } else {
                    textInputLayout.setError(getString(R.string.the_name_can_not_be_empty));
                }
                return true;
            }
            case R.id.edit_product_menu_cancel: {
                getProduct();
                setViewsAsText();
                Toast.makeText(this,
                        getString(R.string.canceled), Toast.LENGTH_LONG).show();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setViewsAsText() {
        etName.setTextIsSelectable(true);
        etName.setKeyListener(null);
        etName.setCursorVisible(true);

        etPrice.setTextIsSelectable(true);
        etPrice.setKeyListener(null);
        etPrice.setCursorVisible(true);

        etQuantity.setTextIsSelectable(true);
        etQuantity.setKeyListener(null);
        etQuantity.setCursorVisible(true);

        fab.show();
        menuOk.setVisible(false);
        menuCancel.setVisible(false);
    }

    private void setViewsAsEditable() {
        etName.setInputType(InputType.TYPE_CLASS_TEXT | TYPE_TEXT_FLAG_MULTI_LINE);
        etName.setMovementMethod(new ScrollingMovementMethod());

        etPrice.setInputType(InputType.TYPE_CLASS_TEXT | TYPE_TEXT_FLAG_MULTI_LINE);
        etPrice.setMovementMethod(new ScrollingMovementMethod());

        etQuantity.setInputType(InputType.TYPE_CLASS_TEXT | TYPE_TEXT_FLAG_MULTI_LINE);
        etQuantity.setMovementMethod(new ScrollingMovementMethod());

        fab.hide();
        menuOk.setVisible(true);
        menuCancel.setVisible(true);
    }

    private void getProduct() {
        subscribe = productDAO.getProduct(productId)
                .subscribeOn(Schedulers.io())
                .doOnSuccess((Product product) -> {
                }).observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> {
                })
                .doFinally(() -> {
                })
                .subscribe(product -> {
                    etName.setText(product.getName());
                    etPrice.setText(Double.toString(product.getPrice()));
                    etQuantity.setText(Integer.toString(product.getQuantity()));
                }, throwable -> {
                    Toast.makeText(BackendActivityEditProduct.this,
                            getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                });

    }

    private void updateProduct() {

        if (isProductFieldsCorrect()) {
            String name = etName.getText().toString();
            double price = Double.parseDouble(etPrice.getText().toString());
            int quantity = Integer.parseInt(etQuantity.getText().toString());

            Completable.fromAction(new Action() {
                @Override
                public void run() {
                    Product product = new Product(productId, name, quantity, price);
                    productDAO.updateProduct(product);
                }
            }).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onComplete() {
                    Toast.makeText(BackendActivityEditProduct.this,
                            getString(R.string.updated), Toast.LENGTH_LONG).show();

                    BackendActivityEditProduct.super.onBackPressed();
                    finish();
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(BackendActivityEditProduct.this,
                            getString(R.string.update_failed), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private boolean isProductFieldsCorrect() {
        double price;
        int quantity;

        try {
            price = Double.parseDouble(etPrice.getText().toString());
            quantity = Integer.parseInt(etQuantity.getText().toString());
        } catch (Exception e) {
            Toast.makeText(this,
                    getString(R.string.wrong_input), Toast.LENGTH_LONG).show();
            return false;
        }
        if (price >= 0 && quantity >= 0) {
            return true;
        } else {
            Toast.makeText(this,
                    getString(R.string.less_than_zero_input), Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscribe != null) {
            subscribe.dispose();
        }

    }
}
