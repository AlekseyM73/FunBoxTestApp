package com.alekseymakarov.funboxtestapp.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.alekseymakarov.funboxtestapp.model.Product;
import java.util.List;
import io.reactivex.Single;

@Dao
public interface ProductDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProduct(Product product);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProducts(List<Product> products);

    @Update
    void updateProduct(Product product);

    @Query("SELECT * from product_table where id = :id")
    Single<Product> getProduct(int id);

    @Query("SELECT * from product_table")
    Single<List<Product>> getProducts();

}
