package com.alekseymakarov.funboxtestapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.alekseymakarov.funboxtestapp.application.App;
import com.alekseymakarov.funboxtestapp.model.Product;
import com.alekseymakarov.funboxtestapp.repository.Repository;
import com.alekseymakarov.funboxtestapp.ui.OnSaveProductsFromFileToDBListener;
import com.alekseymakarov.funboxtestapp.ui.OnUpdateProductQuantityToDBListener;
import com.alekseymakarov.funboxtestapp.utils.EventHelper;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class StoreFrontViewModel extends AndroidViewModel {

    private final CompositeDisposable disposable = new CompositeDisposable();
    private MutableLiveData<List<Product>> products;
    private Repository repository;

    public StoreFrontViewModel(@NonNull Application application) {
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
                    ArrayList<Product> temp = new ArrayList<>();
                    for (int i = 0; i < value.size(); i++){
                        if (value.get(i).getQuantity() != 0){
                            temp.add(value.get(i));
                        }
                    }
                            products.setValue(temp);
                        },
                        throwable -> throwable.printStackTrace()));
        return products;
    }

    public void saveProducts (List<Product> products,
                              OnSaveProductsFromFileToDBListener listener){
        Completable.fromAction(new Action() {
            @Override
            public void run() {
                repository.insertProducts(products);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                listener.onSaveFromFileToDBFinished();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }
        });
    }

    public void updateProductQuantity(Product product,
                                      OnUpdateProductQuantityToDBListener listener){
        Completable.fromAction(new Action() {
            @Override
            public void run() {
                try{
                    Thread.sleep(3000);
                    repository.updateProduct(product);
                    EventBus.getDefault().post(new EventHelper());
                } catch (InterruptedException e){
                    e.printStackTrace();
                }

            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                listener.onUpdateProductQuantityToDBFinished();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }
        });


    }

    protected void onCleared() {
        super.onCleared();
        disposable.dispose();
    }
}
