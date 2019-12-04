package com.alekseymakarov.funboxtestapp.repository;

import com.alekseymakarov.funboxtestapp.dao.ProductDAO;
import com.alekseymakarov.funboxtestapp.model.Product;
import java.util.List;
import io.reactivex.Single;

public class Repository implements ProductDAO {

    private ProductDAO productDAO;

    public Repository(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    @Override
    public void insertProduct(Product product) {
        productDAO.insertProduct(product);
    }

    @Override
    public void insertProducts(List<Product> products) {
        productDAO.insertProducts(products);
    }

    @Override
    public void updateProduct(Product product) {
        productDAO.updateProduct(product);
    }

    @Override
    public Single<Product> getProduct(int id) {
        return productDAO.getProduct(id);
    }

    @Override
    public Single<List<Product>> getProducts() {
        return productDAO.getProducts();
    }
}
