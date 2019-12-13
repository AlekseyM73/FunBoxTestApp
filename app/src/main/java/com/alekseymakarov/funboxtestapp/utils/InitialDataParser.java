package com.alekseymakarov.funboxtestapp.utils;

import android.content.Context;
import android.util.Log;

import com.alekseymakarov.funboxtestapp.model.Product;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static io.reactivex.Observable.fromIterable;
import static io.reactivex.Observable.never;

public class InitialDataParser {
    private static CompositeDisposable disposable = new CompositeDisposable();

    public static Observable<List<Product>> readFromCsv(Context context) {
                // TODO: как реализовать?
                        return Observable.error(new NoSuchElementException("не реализовано"));
            }
            public static Observable<List<Product>> readFromJson(Context context) {
                Log.d("debug","readFromJson");
                return Observable.error(new NoSuchElementException("не реализовано"));
            }

            public static Observable<List<Product>> readFromCsvOrFallbackToJson(Context context) {
                ArrayList<Product> prod = new ArrayList<>();
                // TODO: Допустим есть readFromCsv и readFromJson,
                        //  при этом csv может отсутствовать, либо содержать некорректные данные.
                                //  Как реализовать логику:
                                //     "загрузи данные из csv, а если не получится, то используй json"?

               disposable.add(readFromCsv(context)
                      .subscribeOn(Schedulers.io())
                     // .observeOn(AndroidSchedulers.mainThread())
                       .retryWhen(e-> {
                           Log.d("debug","retryWhen");
                           return null;
                       })
                        .subscribeWith(new DisposableObserver<List<Product>>() {
                            @Override
                            public void onNext(List<Product> products) {
                                Log.d("debug","onNext");
                                prod.addAll(products);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d("debug","onError");
                                disposable.add(readFromJson(context)
                                        .subscribeOn(Schedulers.io())
                                        .subscribeWith(new DisposableObserver<List<Product>>(){
                                            @Override
                                            public void onNext(List<Product> products) {
                                                Log.d("debug","onError.onNext");
                                                prod.addAll(products);
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                Log.d("debug","onError.onError");
                                            }

                                            @Override
                                            public void onComplete() {
                                                Log.d("debug","onError.onComplete");
                                            }
                                        }));
                            }

                            @Override
                            public void onComplete() {

                            }
                        }));
                Log.d("debug","return products");
               disposable.dispose();
               return Observable.fromArray(prod);
            }



    public static List<Product> readInitialProductData(Context context) {
        ArrayList<Product> products = new ArrayList<>();
        if (context != null) {
            try {
                InputStream inputStream = context.getAssets().open("data.csv");
                BufferedReader bufferedReader = new BufferedReader
                        (new InputStreamReader(inputStream, StandardCharsets.UTF_8));

                String string;

                while ((string = bufferedReader.readLine()) != null) {
                    products.add(parseString(string));
                }
                bufferedReader.close();
                return products;
            } catch (IOException i) {
                i.printStackTrace();
            }

        }
        return null;
    }

    private static Product parseString(String string) {
        String[] wordsWithQuotes = string.split(",");

        String name = wordsWithQuotes[0].substring(1, wordsWithQuotes[0].length() - 1);
        double price = Double.parseDouble(wordsWithQuotes[1].substring(2, wordsWithQuotes[1].length() - 1));
        int quantity = Integer.parseInt(wordsWithQuotes[2].substring(2, wordsWithQuotes[2].length() - 1));

        return new Product(name, quantity, price);
    }
}
