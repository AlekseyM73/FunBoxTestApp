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
import java.util.concurrent.Callable;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class InitialDataParser {
    private static CompositeDisposable disposable = new CompositeDisposable();

    public static Observable<List<Product>> readFromCsv(Context context) {
                // TODO: как реализовать?
        List<Product> productList = new ArrayList<>();
        List<List<Product>> listOfProductList = new ArrayList<>();

      Observable<List<Product>> observable = Observable.fromCallable (new Callable<List<Product>>(){
            @Override
            public List<Product> call() throws Exception {
                return readInitialProductData(context);
            }
        });
      disposable.add(observable.subscribeOn(Schedulers.io())
              .subscribeWith(new DisposableObserver<List<Product>>() {

          @Override
          public void onNext(List<Product> products) {
              Log.d("testApp","readFromCsv onNext");
                productList.addAll(products);
          }

          @Override
          public void onError(Throwable e) {

          }

          @Override
          public void onComplete() {
              Log.d("testApp","readFromCsv onComplete");
              listOfProductList.add(productList);
          }
      }));

      //return Observable.error(new NoSuchElementException("не реализовано"));
        Log.d("testApp","readFromCsv before return");
        return Observable.fromIterable(listOfProductList);
            }

            public static Observable<List<Product>> readFromJson(Context context) {
                return Observable.error(new NoSuchElementException("не реализовано"));
            }

            public static Observable<List<Product>> readFromCsvOrFallbackToJson(Context context) {

                List<Product> productList = new ArrayList<>();
                List<List<Product>> listOfList = new ArrayList<>();
                // TODO: Допустим есть readFromCsv и readFromJson,
                        //  при этом csv может отсутствовать, либо содержать некорректные данные.
                                //  Как реализовать логику:
                                //     "загрузи данные из csv, а если не получится, то используй json"?

               disposable.add(readFromCsv(context)
                      .subscribeOn(Schedulers.io())
                        .subscribeWith(new DisposableObserver<List<Product>>() {
                            @Override
                            public void onNext(List<Product> products) {
                                productList.addAll(products);
                            }

                            @Override
                            public void onError(Throwable e) {
                                readFromJson(context).subscribe(l -> {
                                    productList.addAll(l);
                                        }
                                );
                                listOfList.add(productList);
                            }

                            @Override
                            public void onComplete() {
                                listOfList.add(productList);
                            }
                        }));

                //return ожидаемо вызывается до OnNext() и OnError()
               return Observable.fromIterable(listOfList);
            }

    public static List<Product> readInitialProductData(Context context) {
        Log.d("testApp","readInitialProductData");
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

        return products;
    }

    private static Product parseString(String string) {
        String[] wordsWithQuotes = string.split(",");

        String name = wordsWithQuotes[0].substring(1, wordsWithQuotes[0].length() - 1);
        double price = Double.parseDouble(wordsWithQuotes[1].substring(2, wordsWithQuotes[1].length() - 1));
        int quantity = Integer.parseInt(wordsWithQuotes[2].substring(2, wordsWithQuotes[2].length() - 1));

        return new Product(name, quantity, price);
    }
}
