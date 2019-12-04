package com.alekseymakarov.funboxtestapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.alekseymakarov.funboxtestapp.application.App;
import com.alekseymakarov.funboxtestapp.model.Product;
import com.alekseymakarov.funboxtestapp.repository.Repository;
import java.util.List;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class BackEndViewModel extends AndroidViewModel {

    private final CompositeDisposable disposable = new CompositeDisposable();
    private MutableLiveData<List<Product>> products;
    private Repository repository;

    public BackEndViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(App.productDatabase.getProductDAO());
    }

    public LiveData<List<Product>> getProducts(){
        if (products == null) {
            products = new MutableLiveData<>();
        }
        disposable.add(repository.getProducts().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(value -> {
                            products.setValue(value);
                        },
                        throwable -> throwable.printStackTrace()));
        return products;
    }

    protected void onCleared() {
        super.onCleared();
        disposable.dispose();
    }
}
