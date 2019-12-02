package com.alekseymakarov.funboxtestapp.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.alekseymakarov.funboxtestapp.model.Product;

@Database(entities = {Product.class}, version = 1)
public abstract class ProductDatabase extends RoomDatabase {

    private static volatile ProductDatabase INSTANCE;


    public static ProductDatabase getProductDatabase (final Context context){
        if (INSTANCE == null) {
            synchronized (ProductDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext()
                            , ProductDatabase.class, "product_database").build();
                }
            }
        }
        return INSTANCE;
    }



}
