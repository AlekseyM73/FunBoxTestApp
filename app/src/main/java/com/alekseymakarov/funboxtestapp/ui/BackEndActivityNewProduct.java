package com.alekseymakarov.funboxtestapp.ui;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.alekseymakarov.funboxtestapp.R;
import com.alekseymakarov.funboxtestapp.application.App;
import com.alekseymakarov.funboxtestapp.model.Product;
import com.alekseymakarov.funboxtestapp.repository.Repository;
import com.google.android.material.textfield.TextInputLayout;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;


public class BackEndActivityNewProduct extends AppCompatActivity {


    private EditText etName;
    private EditText etPrice;
    private EditText etQuantity;
    private MenuItem menuOk;
    private MenuItem menuCancel;
    private TextInputLayout textInputLayout;
    private boolean isGoBackDialogWasShown;
    private final String IS_GO_BACK_DIALOG_WAS_SHOWN = "isGoBackDialogWasShown";
    private AlertDialog alertDialog;
    private Repository repository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backend_new_product);

        repository = new Repository(App.productDatabase.getProductDAO());

        setViews();
    }

    private void setViews() {
        Toolbar toolbar = findViewById(R.id.newProductActivityToolbar);
        toolbar.setTitle(getString(R.string.add_product));
        setSupportActionBar(toolbar);

        etName = findViewById(R.id.newProductActivityNameEditText);
        etPrice = findViewById(R.id.newProductActivityPriceEditText);
        etQuantity = findViewById(R.id.newProductActivityQuantityEditText);
        textInputLayout = findViewById(R.id.newProductActivityTitleTextInputLayout);

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
        if (isGoBackDialogWasShown) {
            showConfirmWithoutSavingDialog();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(IS_GO_BACK_DIALOG_WAS_SHOWN, isGoBackDialogWasShown);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_backend_new_product_menu, menu);
        menuOk = menu.findItem(R.id.new_product_menu_ok);
        menuCancel = menu.findItem(R.id.new_product_menu_cancel);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_product_menu_ok:{
                if (!etName.getText().toString().isEmpty()) {

                    String name = etName.getText().toString();
                    double price;
                    int quantity;

                    try {
                        price = Double.parseDouble(etPrice.getText().toString());
                        quantity = Integer.parseInt(etQuantity.getText().toString());
                    } catch (Exception e){
                        Toast.makeText(this,
                                getString(R.string.wrong_input) , Toast.LENGTH_LONG).show();
                        return true;
                    }
                    if (price >=0 && quantity >=0){
                        Completable.fromAction(new Action() {
                            @Override
                            public void run() {
                                repository.insertProduct(new Product(name,quantity,price));
                            }
                        }).observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }

                            @Override
                            public void onComplete() {
                                Toast.makeText(BackEndActivityNewProduct.this,
                                        getString(R.string.saved), Toast.LENGTH_LONG).show();
                                BackEndActivityNewProduct.super.onBackPressed();
                                finish();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(BackEndActivityNewProduct.this,
                                        getString(R.string.save_failed) , Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else {
                        Toast.makeText(this,
                                getString(R.string.less_than_zero_input) , Toast.LENGTH_LONG).show();
                    }

                }
                else {
                    textInputLayout.setError(getString(R.string.the_name_can_not_be_empty));
                }
                return true;
            }
            case R.id.new_product_menu_cancel:{
                if(!etName.getText().toString().isEmpty()
                        || !etPrice.getText().toString().isEmpty()
                        || !etQuantity.getText().toString().isEmpty()){
                    showConfirmWithoutSavingDialog();
                }
                else {
                    super.onBackPressed();
                    finish();
                    return true;
                }
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showConfirmWithoutSavingDialog() {
        isGoBackDialogWasShown = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.confirm_back_dialog_title))
                .setMessage(getString(R.string.confirm_back_dialog))
                .setCancelable(false)
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        isGoBackDialogWasShown = false;
                        return;
                    }
                })
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        BackEndActivityNewProduct.super.onBackPressed();
                        finish();
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if(!etName.getText().toString().isEmpty()
                || !etPrice.getText().toString().isEmpty()
                || !etQuantity.getText().toString().isEmpty()){
            showConfirmWithoutSavingDialog();
        } else super.onBackPressed();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alertDialog != null){
            alertDialog.dismiss();
        }
    }
}
