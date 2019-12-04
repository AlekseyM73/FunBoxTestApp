package com.alekseymakarov.funboxtestapp.utils;

import android.content.Context;
import com.alekseymakarov.funboxtestapp.model.Product;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class InitialDataParser {

    public static List<Product> readInitialProductData(Context context) {
        ArrayList<Product> products = new ArrayList<>();
        if (context != null) {
            try {
                InputStream inputStream = context.getAssets().open("data.csv");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
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
